package net.vercte.satchels.api;

import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class SatchelAccess {
    /**
     * <p>
     *     A set of predicates to determine if the Satchel can be accessed.
     *     The satchel can be accessed if <i>any</i> of these are true.
     * </p>
     */
    public static Set<Predicate<Player>> CAN_ACCESS_PREDICATES = new HashSet<>();

    /**
     * A set of predicates to determine if the Satchel is visible.
     * The satchel is visible if <i>all</i> of these are true <i>and</i> if the satchel is accessible (see {@link SatchelAccess#CAN_ACCESS_PREDICATES}).
     * <p>
     *     <b>This should be cached...</b> pretty please with a cherry on top?
     * </p>
     */
    public static Set<Predicate<Player>> IS_VISIBLE_PREDICATES = new HashSet<>();

    /**
     * Checks if a player can access their satchel.
     * @param player The <code>Player</code> that this check concerns.
     * @return Whether the <code>Player</code> can access their satchel.
     */
    public static boolean canAccessSatchel(Player player) {
        return (
                CAN_ACCESS_PREDICATES.isEmpty() ||
                CAN_ACCESS_PREDICATES.stream().anyMatch(p -> p.test(player))
        );
    }

    /**
     * Checks if the satchel model is visible on a player.
     * @param player The <code>Player</code> that this check concerns.
     * @return Whether the satchel model is visible on the player.
     */
    public static boolean satchelIsVisible(Player player) {
        return canAccessSatchel(player) && (
                IS_VISIBLE_PREDICATES.isEmpty() ||
                IS_VISIBLE_PREDICATES.stream().allMatch(p -> p.test(player))
        );
    }
}
