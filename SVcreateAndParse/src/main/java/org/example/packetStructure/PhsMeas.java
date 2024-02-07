package org.example.packetStructure;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhsMeas {
    private int instIa;
    private Quality qIa;

    private int instIb;
    private Quality qIb;

    private int instIc;
    private Quality qIc;

    private int instIn;
    private Quality qIn;


    private int instUa;
    private Quality qUa;

    private int instUb;
    private Quality qUb;

    private int instUc;
    private Quality qUc;

    private int instUn;
    private Quality qUn;

}