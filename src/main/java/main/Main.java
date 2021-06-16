package main;

import main.game_control_files.GameActions;
import main.game_control_files.GameFlow;
import main.game_control_files.PlayState;
import main.non_blackjack_commands.ClearCommandListener;
import main.non_blackjack_commands.HelpCommandListener;
import main.persistence_layer.IPlayerPersistent;
import main.persistence_layer.PlayerPersistent;
import main.registration.RegisterReactionListener;
import main.util.EmbedMessageCreator;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.util.HashSet;
import java.util.Set;


public class Main {
    public static final String REGISTER_CHANNEL_ID = "851209582205468693";
    public static final String PLAY_CHANNEL_ID = "851209654146957312";

    public static void main(String[] args) throws LoginException, InterruptedException {

        JDABuilder jdaBuilder = JDABuilder.createDefault("");
        JDA jda = jdaBuilder.build();
        jda.awaitReady();
        System.out.println("Bot is on");

        IPlayerPersistent playerPersistent = new PlayerPersistent(jda);
        EmbedMessageCreator embedMessageCreator = new EmbedMessageCreator(jda);
        embedMessageCreator.createRegisterEmbedIfNeeded();

        Thread t1 = new Thread(new Shut(jda, playerPersistent));
        t1.start();

        Set<Player> playerSet = new HashSet<>();
        PlayState playState = PlayState.NOT_PLAYING;
        GameActions gameActions = new GameActions(jda);

        jda.addEventListener(new RegisterReactionListener(playerPersistent),
                new GameFlow(playState, playerSet, playerPersistent, gameActions, jda), new HelpCommandListener(), new ClearCommandListener(playState));


    }
}
