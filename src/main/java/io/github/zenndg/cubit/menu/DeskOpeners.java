package io.github.zenndg.cubit.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/** What releases each locked recipe, in the opening data of the desk screen (answer 1970). */
final class DeskOpeners {
  private DeskOpeners() {}

  static void write(FriendlyByteBuf data, Map<ResourceLocation, List<Item>> openers) {
    data.writeMap(
        openers,
        (buffer, recipe) -> buffer.writeResourceLocation(recipe),
        (buffer, items) ->
            buffer.writeCollection(
                items,
                (each, item) -> each.writeResourceLocation(BuiltInRegistries.ITEM.getKey(item))));
  }

  static Map<ResourceLocation, List<Item>> read(FriendlyByteBuf data) {
    return data.readMap(
        buffer -> buffer.readResourceLocation(),
        buffer ->
            buffer.readCollection(
                ArrayList::new, each -> BuiltInRegistries.ITEM.get(each.readResourceLocation())));
  }
}
