package org.example.logiclanodes.common;

import org.example.common.GenDataObject;

/** Абстрактный класс для создания узлов **/
public abstract class LN extends GenDataObject {

    /** Основная функция узла **/
    public abstract void process();
}
