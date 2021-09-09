package main;

import player_entity.Player;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.security.auth.login.LoginException;
import game_control_files.GameFlow;
import game_control_files.PlayState;
import persistence_layer.IPlayerPersistent;
import persistence_layer.PlayerFilePersistent;
import registration.EmbedMessageCreatorRegistration;
import registration.RegisterReactionListener;
import text_commands.HelpCommandListener;

import java.util.HashSet;
import java.util.Set;

import utility.ConfigReader;


public class Main {
    public static final String REGISTER_CHANNEL_ID = "851209582205468693";
    public static final String PLAY_CHANNEL_ID = "851209654146957312";
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws LoginException, InterruptedException {

        ConfigReader configReader = ConfigReader.getInstance();
        JDABuilder jdaBuilder =
                JDABuilder.createDefault(configReader.getToken());
        JDA jda = jdaBuilder.build();
        jda.awaitReady();
        logger.info("Bot is on");

        IPlayerPersistent playerPersistent = new PlayerFilePersistent(jda);
        EmbedMessageCreatorRegistration embedMessageCreatorRegistration = new EmbedMessageCreatorRegistration(jda);
        embedMessageCreatorRegistration.createRegisterEmbedIfNeeded();

        Thread t1 = new Thread(new Shut(jda, playerPersistent));
        t1.start();

        Set<Player> playerSet = new HashSet<>();
        PlayState playState = PlayState.NOT_PLAYING;

        jda.addEventListener(new RegisterReactionListener(playerPersistent),
                new GameFlow(playState, playerSet, playerPersistent,jda),
                new HelpCommandListener());


    }
}
