package jkmau5.alternativeenergy.world.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import jkmau5.alternativeenergy.AltEngCompat;
import jkmau5.alternativeenergy.Constants;
import jkmau5.alternativeenergy.client.render.Render;
import jkmau5.alternativeenergy.world.tileentity.TileEntityPowerCable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.List;

/**
 * Author: Lordmau5
 * Date: 27.10.13
 * Time: 20:56
 * You are allowed to change this code,
 * however, not to publish it without my permission.
 */
public class BlockPowerCable extends BlockTileEntity {

    public BlockPowerCable(Material par2Material) {
        super("powerCable", par2Material);
        setHardness(5.0F);
        setStepSound(soundMetalFootstep);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        return new TileEntityPowerCable();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(Constants.TEXTURE_DOMAIN + ":" + this.getUnlocalizedName().substring(12));
    }

    @Override
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
        return this.getIcon(side, par1IBlockAccess.getBlockMetadata(x, y, z));
    }

    @Override
    public Icon getIcon(int side, int meta)
    {
        return this.blockIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase ent, ItemStack is) {
        super.onBlockPlacedBy(world, x, y, z, ent, is);
        if(!world.isRemote)
            return;

        TileEntityPowerCable pCable = (TileEntityPowerCable) world.getBlockTileEntity(x, y, z);

        if(pCable != null)
            pCable.onNeighborChange();
    }

    @Override
    public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        if(world.isRemote)
            return world.setBlockToAir(x, y, z);

        TileEntityPowerCable tile = (TileEntityPowerCable) world.getBlockTileEntity(x, y, z);
        if(tile != null)
            tile.getEnergyNetwork().recalculateNetworks();

        return world.setBlockToAir(x, y, z);
    }

    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int par5) {
        if(!world.isRemote)
            return;

        for(int i=0; i<6; i++) {
            ForgeDirection dr = ForgeDirection.getOrientation(i);
            TileEntity tmpTile = world.getBlockTileEntity(x + dr.offsetX, y + dr.offsetY, z + dr.offsetZ);
            if(tmpTile == null || !(tmpTile instanceof TileEntityPowerCable))
                continue;

            ((TileEntityPowerCable)tmpTile).getConnectionMatrix().setConnected(dr.getOpposite(), false);
        }
    }

    @Override
    public void onNeighborTileChange(World world, int x, int y, int z, int tileX, int tileY, int tileZ)
    {
        TileEntityPowerCable me = (TileEntityPowerCable) world.getBlockTileEntity(x, y, z);
        if(me == null)
            return;

        if(world.isRemote) {
            me.onNeighborChange();
            return;
        }

        TileEntity xTile = world.getBlockTileEntity(tileX, tileY, tileZ);
        if(xTile == null || AltEngCompat.isInvalidPowerTile(xTile))
            me.getEnergyNetwork().removeInput(me, xTile);
        else
            if(AltEngCompat.isInvalidPowerTile(xTile))
                me.getEnergyNetwork().addInput(me, xTile);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k)
    {
        setCableBoundingBox(world, i, j, k);
    }

    public void setCableBoundingBox(IBlockAccess bAccess, int x, int y, int z)
    {
        TileEntity xTile;
        float minX = 0.335F;
        float minY = 0.335F;
        float minZ = 0.335F;

        float maxX = 0.665F;
        float maxY = 0.665F;
        float maxZ = 0.665F;

        xTile = bAccess.getBlockTileEntity(x - 1, y, z);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                minX = 0.0F;
        }

        xTile = bAccess.getBlockTileEntity(x + 1, y, z);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                maxX = 1.0F;
        }

        xTile = bAccess.getBlockTileEntity(x, y, z - 1);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                minZ = 0.0F;
        }

        xTile = bAccess.getBlockTileEntity(x, y, z + 1);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                maxZ = 1.0F;
        }

        xTile = bAccess.getBlockTileEntity(x, y - 1, z);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                minY = 0.0F;
        }

        xTile = bAccess.getBlockTileEntity(x, y + 1, z);
        if(xTile != null)
        {
            if(AltEngCompat.isValidPowerTile(xTile))
                maxY = 1.0F;
        }

        this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB axisAlignedBB, List list, Entity entity)
    {
        setCableBoundingBox(world, x, y, z);
        AxisAlignedBB aabb = super.getCollisionBoundingBoxFromPool(world, x, y, z);
        if ((aabb != null) && (axisAlignedBB.intersectsWith(aabb)))
        {
            list.add(aabb);
        }
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return Render.RENDER_BLOCKPOWERCABLE;
    }
}