/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.debug;

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;

/**
 *
 * @author gbl
 */
public class NBTdump {
    public static void dump(NBTTagCompound tag, int indent) {
        StringBuilder res;
        for (String s: tag.keySet()) {
            res=new StringBuilder();
            for (int i=0; i<indent; i++)
                res.append("    ");
            INBTBase elem = tag.get(s);
            if (elem.getId() == 8) {
                res.append(s).append(":").append(tag.getString(s));
            } else if (elem.getId() == 2) {
                res.append(s).append(":").append(tag.getShort(s));
            } else if (elem.getId() == 3) {
                res.append(s).append(":").append(tag.getInt(s));
            } else if (elem.getId() == 10) {
                res.append(s).append(": Compound");
            } else {
                res.append(s).append(": Type ").append(elem.getId());
            }
            System.out.println(res);
            if (elem.getId() == 10)
                dump(tag.getCompound(s), indent+1);
        }
    }
}
