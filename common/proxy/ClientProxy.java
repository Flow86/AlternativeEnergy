package proxy;

import client.TileEntityPowerCableRender;
import cpw.mods.fml.client.registry.ClientRegistry;
import tile.TileEntityPowerCable;

/**
 * Author: Lordmau5
 * Date: 28.10.13
 * Time: 10:57
 * You are not allowed to change this code,
 * nor publish it without my permission.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void initRendering()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityPowerCable.class, new TileEntityPowerCableRender());
    }

}