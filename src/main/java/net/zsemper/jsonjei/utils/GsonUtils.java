package net.zsemper.jsonjei.utils;

import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.zsemper.jsonjei.JsonJei;

import java.util.Objects;

public final class GsonUtils extends GsonHelper {
    private GsonUtils() {}

    /**
     * Used to check if a {@link JsonObject} contains a given member, but does not contain any other members
     *
     * @param object The JsonObject to check
     * @param member The member that has to be in the JsonObject
     * @param blacklist The members that should not be in the JsonObject
     * @return true, if only the member is contained in the JsonObject, not any of the blacklisted member
     */
    public static boolean containsExclusive(JsonObject object, String member, String... blacklist) {
        if (object.has(member)) {
            return !containsAll(object, blacklist);
        }
        return false;
    }

    /**
     * Used to check if a {@link JsonObject} contains every given member.
     *
     * @param object The JsonObject to check
     * @param member The members that should be in the object
     * @return true, if all given member are in the JsonObject
     */
    public static boolean containsAll(JsonObject object, String... member) {
        for (String str : member) {
            if (!object.has(str)) {
                return false;
            }
        }
        return true;
    }

    public static ResourceLocation parse(JsonObject object, String name) {
        return ResourceLocation.parse(getAsString(object, name));
    }

    public static Item getItem(JsonObject object, String name) {
        return Utils.getItem(getAsString(object, name));
    }

    public static Fluid getFluid(JsonObject object, String name) {
        ResourceLocation fluidKey = ResourceLocation.parse(getAsString(object, name));
        if (!BuiltInRegistries.FLUID.containsKey(fluidKey)) {
            JsonJei.LOGGER.error("Invalid fluid for key '{}'", name);
            return Fluids.EMPTY;
        }
        return Objects.requireNonNullElse(BuiltInRegistries.FLUID.get(fluidKey), Fluids.EMPTY);
    }
}
