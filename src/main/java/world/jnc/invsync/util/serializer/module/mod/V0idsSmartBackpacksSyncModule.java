package world.jnc.invsync.util.serializer.module.mod;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.Player;
import v0id.api.vsb.capability.IVSBPlayer;
import v0id.api.vsb.capability.VSBCaps;
import world.jnc.invsync.util.serializer.CapabilitySerializer;
import world.jnc.invsync.util.serializer.NativeInventorySerializer;

public class V0idsSmartBackpacksSyncModule extends BaseModSyncModule {
  @Override
  public String getModId() {
    return "v0idssmartbackpacks";
  }

  @Override
  public DataView serialize(Player player, DataView container) {
    return Helper.serialize(player, container);
  }

  @Override
  public void deserialize(Player player, DataView container) {
    Helper.deserialize(player, container);
  }

  @SuppressFBWarnings(
    value = "NP_NULL_PARAM_DEREF_NONVIRTUAL",
    justification = "Capabilities aren't null during runtime (but compile time)."
  )
  @UtilityClass
  private static class Helper {
    private static final DataQuery PLAYER_DATA = DataQuery.of("player_data");

    private static DataView serialize(Player player, DataView container) {
      final EntityPlayer nativePlayer = NativeInventorySerializer.getNativePlayer(player);

      container.set(
          PLAYER_DATA,
          CapabilitySerializer.serializeCapabilityToData(VSBCaps.PLAYER_CAPABILITY, nativePlayer));

      return container;
    }

    private static void deserialize(Player player, DataView container) {
      Optional<DataView> player_data = container.getView(PLAYER_DATA);

      if (player_data.isPresent()) {
        final EntityPlayer nativePlayer = NativeInventorySerializer.getNativePlayer(player);

        CapabilitySerializer.deserializeCapabilityFromData(
            VSBCaps.PLAYER_CAPABILITY, nativePlayer, player_data.get());
        IVSBPlayer vsb_player = IVSBPlayer.of(nativePlayer);
        if (vsb_player != null) vsb_player.sync();
      }

      if (getDebug()) {
        getLogger().info("\t\tisPresent:");
        getLogger().info("\t\t\tplayer_data:\t" + player_data.isPresent());
      }
    }
  }
}
