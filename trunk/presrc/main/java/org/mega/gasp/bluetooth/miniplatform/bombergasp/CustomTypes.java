/***
 * BomberGASP: a GASP Bluetooth (GASPBT) middleware based "Bomberman-like" game.
 * Copyright (C) 2006 CNAM/INT
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Contact: gasp-team@objectweb.org
 *
 * Author: Sebire - Negre
 */
/**
 * This was modified no later than 2009-05-26 by Irving Bunton, Jr
 */

// Expand to define DJSR82 define
@DJSR82@
//#ifdef DJSR82
package org.mega.gasp.bluetooth.miniplatform.bombergasp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Hashtable;

/**
 *
 * @author Sebire - Negre
 */
public class CustomTypes implements org.mega.gasp.bluetooth.miniplatform.CustomTypes{
    
    /** Creates a new instance of CustomTypes */
    public CustomTypes() {
    }
    
    public void encodeUpdate(DataOutputStream dos,Update customType) throws Exception { 
	customType.drawOutgoing();
	dos.writeInt(customType.getId());
    	dos.writeInt(customType.getX());
        dos.writeInt(customType.getY());
        dos.writeInt(customType.getDir());
        dos.writeBoolean(customType.getMov());
    }
    
    public void encodeDelete(DataOutputStream dos,Delete customType) throws Exception { 
	customType.draw();
	dos.writeInt(customType.getId());
        Utils.logGASPBT("id"+customType.getId());
    }
    
    public void encodeDeleteTiles(DataOutputStream dos,DeleteTiles customType) throws Exception { 
        customType.draw();
	dos.writeInt(customType.getM());
        dos.writeInt(customType.getN());
    }

     public void encodeDeleteBonus(DataOutputStream dos,DeleteBonus customType) throws Exception { 
        customType.draw();
	dos.writeInt(customType.getId());
        dos.writeInt(customType.x);
    dos.writeInt(customType.y);
         dos.writeInt(customType.type);
     }
     
     public void encodePutBomb(DataOutputStream dos,PutBomb customType) throws Exception { 
        customType.draw();
	dos.writeInt(customType.getX());
        dos.writeInt(customType.getY());
        dos.writeInt(customType.getPuis());
    }
    
     public void encodeExplodeBomb(DataOutputStream dos,ExplodeBomb customType) throws Exception { 
        customType.draw();
	dos.writeInt(customType.getX());
        dos.writeInt(customType.getY());
    }
     
          public void encodeDoItems(DataOutputStream dos,DoItems customType) throws Exception { 
        customType.draw();
	dos.writeUTF(customType.getItems());
    }
    
   public void encodeData(Hashtable h, DataOutputStream dos) throws Exception {
		Utils.logGASPBT("********** ENCODE DATA Log4J CustomTypes Category  instanciated ***********");
		int nb = h.size();
		dos.writeByte(nb);
		Utils.logGASPBT("Write short on dis:"+h.size());
		Object key = (Object) h.keys().nextElement();
		Utils.logGASPBT("First object key: "+key);
		Utils.logGASPBT("First object key type: "+key.getClass().getName());
		Utils.logGASPBT("First object: "+h.elements().nextElement());
		
		for(int i = 0; i < nb; i++){    
			
		    Object o = (Object) h.get(""+i);
			Utils.logGASPBT("loop: object> "+o);
			Utils.logGASPBT("loop: class> "+ o.getClass().getName());
			if (o instanceof Update){
			    Utils.logGASPBT("Start to encore Update object ");
				dos.writeByte(6);
				encodeUpdate(dos,(Update) o);
				Utils.logGASPBT("Finish to encore Update object ");
			}
			if (o instanceof Delete){
				dos.writeByte(7);
				encodeDelete(dos,(Delete) o);
			}
			if (o instanceof DeleteTiles){
				dos.writeByte(8);
				encodeDeleteTiles(dos,(DeleteTiles) o);
			}
			if (o instanceof DeleteBonus){
				dos.writeByte(9);
				encodeDeleteBonus(dos,(DeleteBonus) o);
			}
                        if (o instanceof PutBomb){
				dos.writeByte(10);
				encodePutBomb(dos,(PutBomb) o);
			}
                        if (o instanceof ExplodeBomb){
				dos.writeByte(11);
				encodeExplodeBomb(dos,(ExplodeBomb) o);
			}
                         if (o instanceof DoItems){
				dos.writeByte(12);
				encodeDoItems(dos,(DoItems) o);
			}
		}
	}

   public Update decodeUpdate(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding update");
	Update customType = new Update();
	customType.setId(dis.readInt());
	Utils.logGASPBT("id="+customType.getId());
	customType.setX(dis.readInt()); 
	Utils.logGASPBT("X="+customType.getX());
	customType.setY(dis.readInt());
	Utils.logGASPBT("Y="+customType.getY());
        customType.setDir(dis.readInt());
	Utils.logGASPBT("Dir="+customType.getDir());
            customType.setMov(dis.readBoolean());
	Utils.logGASPBT("Moving="+customType.getMov());
	customType.drawIncoming();
	return customType;
    }
   
   public Delete decodeDelete(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a delete");
	Delete customType = new Delete();
	customType.setId(dis.readInt()); 
	Utils.logGASPBT("id="+customType.getId());
	return customType;
    }
   
    public DeleteTiles decodeDeleteTiles(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a deleteTiles");
	DeleteTiles customType = new DeleteTiles();
	customType.setM(dis.readInt()); 
	Utils.logGASPBT("M="+customType.getM());
	customType.setN(dis.readInt());
	Utils.logGASPBT("N="+customType.getN());
	customType.draw();
	return customType;
    }
    
    public DeleteBonus decodeDeleteBonus(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a deleteBonus");
	DeleteBonus customType = new DeleteBonus();
        //faire une methode qui renvoi l'id du bonus a supprimer
	customType.setId(dis.readInt()); 
	Utils.logGASPBT("M="+customType.getId());
        	customType.x=(dis.readInt()); 
	Utils.logGASPBT("x="+customType.x);
                	customType.y=(dis.readInt()); 
	Utils.logGASPBT("y="+customType.y);
                	customType.type=(dis.readInt()); 
	Utils.logGASPBT("type="+customType.type);
	customType.draw();
	return customType;
    }
    
     public PutBomb decodePutBomb(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a putBomb");
	PutBomb customType = new PutBomb();
	customType.setX(dis.readInt()); 
	Utils.logGASPBT("X="+customType.getX());
	customType.setY(dis.readInt());
	Utils.logGASPBT("Y="+customType.getY());
        customType.setPuis(dis.readInt());
	Utils.logGASPBT("Puissance="+customType.getPuis());
	//customType.draw();
	return customType;
    }
    
     public ExplodeBomb decodeExplodeBomb(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a explodeBomb");
	ExplodeBomb customType = new ExplodeBomb();
	customType.setX(dis.readInt()); 
	Utils.logGASPBT("X="+customType.getX());
	customType.setY(dis.readInt());
	Utils.logGASPBT("Y="+customType.getY());
	customType.draw();
	return customType;
    }
   
        public DoItems decodeDoItems(DataInputStream dis) throws Exception {
	Utils.logGASPBT("Start decoding a DoItems");
	DoItems customType = new DoItems();
	customType.setItems(dis.readUTF()); 
	Utils.logGASPBT("id="+customType.getItems());
	return customType;
    }
     
   public Hashtable decodeData(DataInputStream dis) throws Exception {
		Utils.logGASPBT("********** DECODE DATA Log4J CustomTypes Category  instanciated ************");
		Hashtable h = new Hashtable();
		byte nb = dis.readByte();
		Utils.logGASPBT("decodeData called");
		Utils.logGASPBT("Nb of objects in the hashtable: "+nb);
		int index = 0;
		while(nb>0){
			byte type = dis.readByte();
			Utils.logGASPBT("Object type: "+type);
			if (type == 6){
				Utils.logGASPBT("1 update object");
				Update customType = decodeUpdate(dis);
				Utils.logGASPBT("Update decoding ok");
				h.put(""+index,customType);
			}
			else if (type == 7){
				Utils.logGASPBT("1 delete object");
				Delete customType = decodeDelete(dis);
				Utils.logGASPBT("Delete decoding ok");
				h.put(""+index,customType);
			}
			else if (type == 8){
				Utils.logGASPBT("1 deleteTiles object");
				DeleteTiles customType = decodeDeleteTiles(dis);
				Utils.logGASPBT("DeleteTiles decoding ok");
				h.put(""+index,customType);
			}
			else if (type == 9){
				Utils.logGASPBT("1 deleteBonus object");
				DeleteBonus customType = decodeDeleteBonus(dis);
				Utils.logGASPBT("DeleteBonus decoding ok");
				h.put(""+index,customType);
			}
                        else if (type == 10){
				Utils.logGASPBT("1 putBomb object");
				PutBomb customType = decodePutBomb(dis);
				Utils.logGASPBT("PutBomb decoding ok");
				h.put(""+index,customType);
			}
                          else if (type == 11){
				Utils.logGASPBT("1 ExplodeBomb object");
				ExplodeBomb customType = decodeExplodeBomb(dis);
				Utils.logGASPBT("ExplodeBomb decoding ok");
				h.put(""+index,customType);
			}
                          else if (type == 12){
				Utils.logGASPBT("1 DoItems object");
				DoItems customType = decodeDoItems(dis);
				Utils.logGASPBT("DoItems decoding ok");
				h.put(""+index,customType);
			}
			nb--;
			index++;
		}
		return h; 
	}
    
}
//#endif
