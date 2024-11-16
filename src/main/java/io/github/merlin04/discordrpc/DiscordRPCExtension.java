package io.github.merlin04.discordrpc;

import com.bitwig.extension.controller.api.Application;
import com.bitwig.extension.controller.api.ControllerHost;

import dev.firstdark.rpc.DiscordRpc;
import dev.firstdark.rpc.enums.ActivityType;
import dev.firstdark.rpc.enums.ErrorCode;
import dev.firstdark.rpc.exceptions.UnsupportedOsType;
import dev.firstdark.rpc.handlers.DiscordEventHandler;
import dev.firstdark.rpc.models.DiscordJoinRequest;
import dev.firstdark.rpc.models.DiscordRichPresence;
import dev.firstdark.rpc.models.User;

import com.bitwig.extension.controller.ControllerExtension;
import com.bitwig.extension.controller.api.DocumentState;
import com.bitwig.extension.controller.api.SettableEnumValue;
import com.bitwig.extension.controller.api.StringValue;

public class DiscordRPCExtension extends ControllerExtension
{
   private static final String[] enabledOpts = { "On", "Off" };
   private SettableEnumValue enabled;
   private DiscordRpc rpc;
   private ControllerHost host;
   private StringValue projectName;
   private StringValue panelLayout;
   private long projectOpenedTime;
   private boolean isDisconnected = true;

   protected DiscordRPCExtension(final DiscordRPCExtensionDefinition definition, final ControllerHost host)
   {
      super(definition, host);
   }
   
   private void disconnect() {
       rpc.shutdown();
       isDisconnected = true;
   }

   private void updatePresence() {
      if(!this.enabled.get().equals(enabledOpts[0])) return;
      String pn = this.projectName.get();
      if(pn.equals("")) {
          if(!isDisconnected) this.disconnect();
          return;
      }
      if(isDisconnected) {
          this.discordConnect();
      }
      String panel = this.panelLayout.get();
      DiscordRichPresence p = DiscordRichPresence.builder()
         .details(pn + ".bwproject")
         .state(panel.equals(Application.PANEL_LAYOUT_ARRANGE) ? "Arranging"
            : panel.equals(Application.PANEL_LAYOUT_MIX) ? "Mixing"
            : panel.equals(Application.PANEL_LAYOUT_EDIT) ? "Editing"
            : "")
         .largeImageKey("bitwig_studio_logo_rgb")
         .largeImageText("Bitwig Studio")
         .startTimestamp(projectOpenedTime)
         .activityType(ActivityType.PLAYING)
         .build();
      rpc.updatePresence(p);
   }

   private void updateTimestamp() {
      projectOpenedTime = System.currentTimeMillis() / 1000L;
   }

   private void discordConnect() {
      DiscordEventHandler handler = new DiscordEventHandler() {
         @Override
         public void ready(User user) {
            host.println("handler ready");
         }

         @Override
         public void disconnected(ErrorCode arg0, String arg1) {
            host.println("disconnected");
         }

         @Override
         public void errored(ErrorCode arg0, String arg1) {
            host.println("errored");
         }

         @Override
         public void joinGame(String arg0) {
         }

         @Override
         public void joinRequest(DiscordJoinRequest arg0) {
         }

         @Override
         public void spectateGame(String arg0) {
         }
      };

      try {
         rpc.init("1306910213659561984", handler, false);
         isDisconnected = false;
         host.println("Discord RPC initialized");
      } catch (UnsupportedOsType e) {
         host.errorln("Unsupported OS");
      }
   }

   @Override
   public void init()
   {
      host = getHost();

      rpc = new DiscordRpc();

      DocumentState documentState = host.getDocumentState();
      Application app = host.createApplication();

      projectName = app.projectName();
      projectName.addValueObserver(_value -> { updateTimestamp(); updatePresence(); });
      panelLayout = app.panelLayout();
      panelLayout.addValueObserver(_value -> updatePresence());
      enabled = documentState.getEnumSetting("Enabled", "opts", enabledOpts, enabledOpts[0]);
      enabled.addValueObserver(value -> {
         host.println(value);
         if(value.equals(enabledOpts[0])) {
            // on
            host.println("updating");
            updateTimestamp();
            this.updatePresence();
         } else {
            host.println("shutting down");
            // off
            this.disconnect();
         }
      });
   }

   @Override
   public void exit()
   {
      this.disconnect();
   }

   @Override
   public void flush()
   {
   }

}
