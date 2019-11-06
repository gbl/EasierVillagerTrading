/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.mcmod.debug;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;


/**
 *
 * @author gbl
 */
public class NBTdump {
    public static void dump(CompoundTag tag, int indent) {
        StringBuilder res;
        for (String s: tag.getKeys()) {
            res=new StringBuilder();
            for (int i=0; i<indent; i++)
                res.append("    ");
            Tag elem = tag.getTag(s);
            if (elem.getType() == 8) {
                res.append(s).append(":").append(tag.getString(s));
            } else if (elem.getType() == 2) {
                res.append(s).append(":").append(tag.getShort(s));
            } else if (elem.getType() == 3) {
                res.append(s).append(":").append(tag.getInt(s));
            } else if (elem.getType() == 10) {
                res.append(s).append(": Compound");
            } else {
                res.append(s).append(": Type ").append(elem.getType());
            }
            System.out.println(res);
            if (elem.getType() == 10)
                dump(tag.getCompound(s), indent+1);
        }
    }
}
