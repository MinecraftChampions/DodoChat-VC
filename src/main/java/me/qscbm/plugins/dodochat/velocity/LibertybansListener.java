package me.qscbm.plugins.dodochat.velocity;

import io.github.minecraftchampions.dodoopenjava.api.v2.ChannelMessageApi;
import me.qscbm.plugins.dodochat.common.Config;
import me.qscbm.plugins.dodochat.common.hook.Hook;
import me.qscbm.plugins.dodochat.common.hook.LuckPermsHook;
import space.arim.libertybans.api.PlayerVictim;
import space.arim.libertybans.api.event.PunishEvent;
import space.arim.libertybans.api.punish.DraftPunishment;
import space.arim.omnibus.events.EventConsumer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LibertybansListener implements EventConsumer<PunishEvent> {
    @Override
    public void accept(PunishEvent e) {
        DraftPunishment p  = e.getDraftPunishment();
        long timeLong = p.getDuration().toMillis();
        String time;
        // 如果想要指定类型的话这边是可以加个判断的
        switch (p.getType()) {
            case BAN -> {
                if (timeLong == 0) {
                    time = "永久封禁";
                } else {
                    time = "封禁" + timeLong / 24 / 60 / 60 + "天";
                }
            }
            case MUTE -> {
                if (timeLong == 0) {
                    time = "永久禁言";
                } else {
                    time = "禁言" + timeLong / 24 / 60 / 60 + "天";
                }
            }
            case WARN -> time = "警告一次";
            case KICK -> time = "踢出服务器";
            default -> {
                try {
                    ChannelMessageApi.sendTextMessage(Config.authorization, Config.getConfiguration().getString("settings.LibertyBansListenerMessage"), "获取数据库失败");
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            }
        }
        String message = null;
        try {
            message = "玩家" + LuckPermsHook.luckPerms.getUserManager().lookupUsername(((PlayerVictim)p.getVictim()).getUUID()).get() + "因" + p.getReason() + "被判" + time;
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException(ex);
        }
        try {
            ChannelMessageApi.sendTextMessage(Config.authorization, Config.getConfiguration().getString("settings.LibertyBansListenerMessage"),message);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}