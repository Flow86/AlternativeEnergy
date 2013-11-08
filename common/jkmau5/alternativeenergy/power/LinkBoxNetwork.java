package jkmau5.alternativeenergy.power;

import jkmau5.alternativeenergy.Config;
import jkmau5.alternativeenergy.world.tileentity.TileEntityLinkBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: Lordmau5
 * Date: 02.11.13
 * Time: 23:20
 * You are allowed to change this code,
 * however, not to publish it without my permission.
 */
public class LinkBoxNetwork {

    public Map<String, ArrayList<TileEntityLinkBox>> linkBoxes;
    public Map<String, Boolean> initiatedNBTPower;
    public Map<String, Integer> networkPower;

    public LinkBoxNetwork() {
        initNetwork();
    }

    public void initNetwork() {
        linkBoxes = new HashMap<String, ArrayList<TileEntityLinkBox>>();
        initiatedNBTPower = new HashMap<String, Boolean>();
        networkPower = new HashMap<String, Integer>();
    }

    public void addLinkBoxToNetwork(TileEntityLinkBox linkBox, String id) {
        ArrayList<TileEntityLinkBox> list = linkBoxes.get(id);
        if(list == null) {
            list = new ArrayList<TileEntityLinkBox>();
        }
        list.add(linkBox);
        linkBoxes.put(id, list);
    }

    public void initiateNetworkPower(String linkId, int power) {
        if(initiatedNBTPower.get(linkId) == null || initiatedNBTPower.get(linkId) == false) {
            initiatedNBTPower.put(linkId, true);
            setNetworkPower(linkId, power);
        }
    }

    public void removeFromLink(TileEntityLinkBox linkBox, String oldLinkID) {
        ArrayList<TileEntityLinkBox> list = linkBoxes.get(oldLinkID);
        if(list == null)
            return;
        list.remove(linkBox);
        linkBoxes.put(oldLinkID, list);
        if(list.size() == 0)
            setNetworkPower(oldLinkID, 0);
    }

    public int neededPower(String linkId) {
        int power = getNetworkPower(linkId);
        return Config.powerBox_capacity - power;
    }

    public void setNetworkPower(String linkId, int power) {
        if(power > Config.powerBox_capacity)
            power = Config.powerBox_capacity;
        networkPower.put(linkId, power);
    }

    public int getNetworkPower(String linkId) {
        if(networkPower.get(linkId) == null)
            return 0;
        return networkPower.get(linkId);
    }
}