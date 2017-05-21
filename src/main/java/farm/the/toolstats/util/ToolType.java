package farm.the.toolstats.util;

import org.bukkit.Material;

public enum ToolType {
    IGNORED,
    // tools
    AXE, FISHING_ROD, FLINT_AND_STEEL, HOE, PICKAXE, SHEARS, SPADE,
    // weapons
    BOW, SHIELD, SWORD;

    /**
     * Groups different tool materials (diamond, gold, iron, ...) together to generalized tool types (pickaxe, axe, spade, sword, ...)
     *
     * @param type used type
     * @return generalized tool type
     */
    public static ToolType getByMaterial(Material type) {
        switch (type) {
            // tools
            case DIAMOND_PICKAXE:
            case GOLD_PICKAXE:
            case IRON_PICKAXE:
            case STONE_PICKAXE:
            case WOOD_PICKAXE:
                return PICKAXE;
            case DIAMOND_AXE:
            case GOLD_AXE:
            case IRON_AXE:
            case STONE_AXE:
            case WOOD_AXE:
                return AXE;
            case DIAMOND_SPADE:
            case GOLD_SPADE:
            case IRON_SPADE:
            case STONE_SPADE:
            case WOOD_SPADE:
                return SPADE;
            case DIAMOND_HOE:
            case GOLD_HOE:
            case IRON_HOE:
            case STONE_HOE:
            case WOOD_HOE:
                return HOE;
            case FISHING_ROD:
                return FISHING_ROD;
            case FLINT_AND_STEEL:
                return FLINT_AND_STEEL;
            case SHEARS:
                return SHEARS;
            // weapons
            case DIAMOND_SWORD:
            case GOLD_SWORD:
            case IRON_SWORD:
            case STONE_SWORD:
            case WOOD_SWORD:
                return SWORD;
            case BOW:
                return BOW;
            case SHIELD:
                return SHIELD;
            default:
                return IGNORED;
        }
    }

    boolean canBeInOffhand() {
        boolean canBeInOffhand = false;
        switch (this) {
            case BOW:
            case SHIELD:
            case FLINT_AND_STEEL:
            case SHEARS:
            case FISHING_ROD:
            case HOE:
                canBeInOffhand = true;
                break;
            // can't be used in offhand: PICKAXE, AXE, SPADE, SWORD
        }
        return canBeInOffhand;
    }
}
