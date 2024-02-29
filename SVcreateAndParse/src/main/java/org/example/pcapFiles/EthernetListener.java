package org.example.pcapFiles;

import com.opencsv.CSVWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.logiclanodes.common.LN;
import org.example.logiclanodes.measurements.MMXU;
import org.example.packetStructureCapture.SvPacket;
import org.pcap4j.core.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class EthernetListener extends LN {


    @Getter @Setter
    private String nickName;
    @Getter @Setter
    private ArrayList<String> nicArray = new ArrayList<>();
    @Getter @Setter
    private ArrayList<String> nicMAC = new ArrayList<>();
    @Getter @Setter
    private ArrayList<String> nicIP = new ArrayList<>();
    @Getter @Setter
    private PcapHandle handle;
    @Getter @Setter
    private static final int BUFFER_SIZE = 1000;
    @Getter @Setter
    private static LinkedBlockingQueue<String> csvBuffer = new LinkedBlockingQueue<>(BUFFER_SIZE);
    HashMap<Integer, SvPacket> svPacketMap = new LinkedHashMap<>();
//    int lastVal = 0;

    MMXU mmxu = new MMXU();


    final List <PacketListener> listeners = new CopyOnWriteArrayList<>();

    private PacketListener defaultPacketListener = packet -> {
       listeners.forEach(listener -> listener.gotPacket(packet));
    };

    public void checkNic(){
        try {
            for (PcapNetworkInterface nic : Pcaps.findAllDevs()) {
//                System.out.println(nic.getAddresses());
//                System.out.println(nic.getLinkLayerAddresses());
                nicMAC.add(String.valueOf(nic.getLinkLayerAddresses().get(0)));
                nicArray.add(nic.getDescription());
                nicIP.add(parseIP(String.valueOf(nic.getAddresses())));
            }
            System.out.println("NIC written down");
        } catch (PcapNativeException e) {
            throw new RuntimeException(e);
        }
    }

    public String parseIP(String ipString){
        String IPADDRESS_PATTERN =
                "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

        Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(ipString);
        if (matcher.find()) {
           return matcher.group();
        }
        else{
           return "0.0.0.0";
        }
    }

    @SneakyThrows
    public void start(){
        if (nickName != null && !nickName.isEmpty()){
            initializeNetworkInterface();

                if (handle != null) {
                    String filter = "ether proto 0x88ba";  //  proto 0x88ba = IEC SV

                    handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

                    Thread captureThread = new Thread(() -> {
                        try {
                            log.info("Starting Packet Capturing");
                            handle.loop(0, defaultPacketListener); //packetCount should be x2. 0 for unlimited capture
                        } catch (PcapNativeException e) {
                            log.info("Finished Packet Capturing");
                        } catch (InterruptedException e) {
                            log.info("Finished Packet Capturing");
                        } catch (NotOpenException e) {
                            log.info("Finished Packet Capturing");
                        }


                    });

                    captureThread.start();
                }
        } else log.error("Select NIC first");
    }

    @SneakyThrows
    public void stop(){
        if (nickName != null && !nickName.isEmpty()) {
            if (handle != null) {
                handle.breakLoop();
//                handle.close();
                log.info("Packet Capturing Stopped");
            }else {
                log.error("Packet Capturing ALREADY Stopped");
            }
        } else log.error("Select NIC first");
    }

    @SneakyThrows
    public void initializeNetworkInterface() {
        Optional<PcapNetworkInterface> nic = Pcaps.findAllDevs().stream()
                .filter(i -> nickName.equals(i.getDescription()))
                .findFirst();

        if (nic.isPresent()) {
            handle = nic.get().openLive(1500, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
            log.info("Network handler: {}", nic);
        }
        else {
            log.error("Network Interface not found");
        }
    }

    public void addListener(PacketListener listener) {
        listeners.add(listener);
    }

    @SneakyThrows
    @Override
    public void process() {
        log.debug("EtListener method");

        File resultCSV = new File("src/main/resources/TestRun.csv");
        FileWriter fileWriter = new FileWriter(resultCSV);
        CSVWriter writer = new CSVWriter(fileWriter);

        writer.writeNext(new String[]{
                "time",
                "source",
                "destination",
                "svID",
                "smpCnt",
                "Ia",
                "Ia_quality",
                "Ib",
                "Ib_quality",
                "Ic",
                "Ic_quality",
                "Ua",
                "Ua_quality",
                "Ub",
                "Ub_quality",
                "Uc",
                "Uc_quality",
        });




        this.checkNic();
        this.setNickName("VMware Virtual Ethernet Adapter for VMnet8");
        this.getNicArray();


        SvParser svParser = new SvParser();

        AtomicInteger curCnt = new AtomicInteger();

//        HashMap<String, ArrayList<SvPacket>> sourceMap = new HashMap<>();

//        ArrayList<Optional> array = new ArrayList<>();

        this.addListener(packet -> {
            Optional<SvPacket> svPacket = svParser.decode(packet);
            int noASDU = svPacket.get().getApdu().getNoASDU();
            for (int i = 0; i < noASDU; i++) {


                if (svPacket.isPresent()
                        && curCnt.get() != svPacket.get().getApdu().getSeqASDU().get(i).getSmpCnt()
//                        && lastVal != svPacket.get().getApdu().getSeqASDU().get(i).getDataset().getInstIa()
                ) {


                    svPacketMap.put(svPacketMap.size(), svPacket.get());
                    mmxu.process(svPacket.get().getApdu().getSeqASDU().get(0).getDataset());
                    System.out.println(svPacketMap.size());

//                    if (svPacketMap.size() == 7199){
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                        System.out.println(String.format("Captured packets: %d", svPacketMap.size()));
//                        for (int j = 0; j < svPacketMap.size(); j++) {
//
//                            writer.writeNext(new String[]{
//                                    String.valueOf(svPacketMap.get(j).getTimestamp()),
//                                    String.valueOf(svPacketMap.get(j).getMacSrs()),
//                                    String.valueOf(svPacketMap.get(j).getMacDst()),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getSvID()),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getSmpCnt()),
//                                    String.valueOf(mmxu.IphsA.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIa().toString()),
//                                    String.valueOf(mmxu.IphsB.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIb().toString()),
//                                    String.valueOf(mmxu.IphsC.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIc().toString()),
//                                    String.valueOf(mmxu.UphsA.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUa().toString()),
//                                    String.valueOf(mmxu.UphsB.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUb().toString()),
//                                    String.valueOf(mmxu.UphsC.get(j)[0]),
//                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUc().toString())
//                            });
//                        }
//                        try {
//                            writer.close();
//                            stop();
//                            Thread.interrupted();
//
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }

                    if (svPacketMap.size() == 7199){
                        System.out.println("DONE");
                        for (int j = 0; j < svPacketMap.size(); j++) {

                            writer.writeNext(new String[]{
                                    String.valueOf(svPacketMap.get(j).getTimestamp()),
                                    String.valueOf(svPacketMap.get(j).getMacSrs()),
                                    String.valueOf(svPacketMap.get(j).getMacDst()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getSvID()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getSmpCnt()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstIa()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIa().toString()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstIb()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIb().toString()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstIc()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQIc().toString()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstUa()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUa().toString()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstUb()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUb().toString()),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getInstUc()/1000d),
                                    String.valueOf(svPacketMap.get(j).getApdu().getSeqASDU().get(0).getDataset().getQUc().toString())
                            });
                        }
                       try {
                            writer.close();
                            stop();
                            Thread.interrupted();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }


//                    if (setSvPckt.size() >= 37000) {
//                        System.out.println(setSvPckt);
//                    }

                    curCnt.set(svPacket.get().getApdu().getSeqASDU().get(i).getSmpCnt());  //update counter else writes packet twice
                }

            }

        });

        this.start();
    }



    public static synchronized void addToBuffer(String data) {
        try {
            csvBuffer.put(data);
        } catch (InterruptedException e) {
            System.err.println("Error adding data to buffer: " + e.getMessage());
        }
    }

    public static void handlePacket(Optional<SvPacket> svPacket) {
        // Extract the relevant information from the packet

        // Format the data as a CSV string


        // Add the data to the buffer
//        addToBuffer(csvData);
    }

//    public static void startWriterThread(String fileName) {
//        Thread writerThread = new Thread(() -> {
//            try (FileWriter fileWriter = new FileWriter(fileName, true)) {
//                while (true) {
//                    String data = csvBuffer.take();
//                    fileWriter.append(data);
//                    fileWriter.append("\n");
//                    fileWriter.flush();
//                }
//            } catch (IOException | InterruptedException e) {
//                System.err.println("Error writing data to CSV file: " + e.getMessage());
//            }
//        });
//        writerThread.start();
//    }


}
