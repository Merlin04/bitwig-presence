package io.github.merlin04.discordrpc;
import java.util.UUID;

import com.bitwig.extension.api.PlatformType;
import com.bitwig.extension.controller.AutoDetectionMidiPortNamesList;
import com.bitwig.extension.controller.ControllerExtensionDefinition;
import com.bitwig.extension.controller.api.ControllerHost;

public class DiscordRPCExtensionDefinition extends ControllerExtensionDefinition
{
   private static final UUID DRIVER_ID = UUID.fromString("1f8b85a3-90e3-49ed-b6d3-639e1a491dcb");
   
   public DiscordRPCExtensionDefinition()
   {
   }

   @Override
   public String getName()
   {
      return "DiscordRPC";
   }
   
   @Override
   public String getAuthor()
   {
      return "Merlin04";
   }

   @Override
   public String getVersion()
   {
      return "0.1.1";
   }

   @Override
   public UUID getId()
   {
      return DRIVER_ID;
   }
   
   @Override
   public String getHardwareVendor()
   {
      return "Discord";
   }
   
   @Override
   public String getHardwareModel()
   {
      return "Discord RPC";
   }

   @Override
   public int getRequiredAPIVersion()
   {
      return 19;
   }

   @Override
   public int getNumMidiInPorts()
   {
      return 0;
   }

   @Override
   public int getNumMidiOutPorts()
   {
      return 0;
   }

   @Override
   public void listAutoDetectionMidiPortNames(final AutoDetectionMidiPortNamesList list, final PlatformType platformType)
   {
   }

   @Override
   public DiscordRPCExtension createInstance(final ControllerHost host)
   {
      return new DiscordRPCExtension(this, host);
   }
}
