package com.group_finity.mascot;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import com.group_finity.mascot.config.BehaviourName;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.config.ConfigurationFactory;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;

/**
 * プログラムのエントリポイント.
 */
public class Main {

	private static final Logger log = Logger.getLogger(Main.class.getName());

	static final BehaviourName BEHAVIOR_GATHER = BehaviourName.ChaseMouse;

	static {
		try {
			LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
		} catch (final SecurityException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static Main instance = new Main();

	public static Main getInstance() {
		return instance;
	}

	private final Manager manager = new Manager();

	private final ConfigurationFactory configurationFactory;
	private Configuration configuration;
	
	public Main() {
		configurationFactory = new ConfigurationFactory();
		configurationFactory.init();
	}

	public static void main(final String[] args) {
		System.setProperty("jna.nosys", "true");
		getInstance().run();
	}

	public void run() {

		// 設定を読み込む
		loadConfiguration();

		// トレイアイコンを作成する
		if (SystemTray.isSupported()) {
			createTrayIcon();
		}

		// しめじを一匹作成する
		createMascot();
		getManager().start();
	}

	/**
	 * 設定ファイルを読み込む.
	 */
	private void loadConfiguration() {

		try {			
			configuration = configurationFactory.load();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error in configuration file", e);
			exit();
		}
	}

	/**
	 * トレイアイコンを作成する.
	 * @throws AWTException
	 * @throws IOException
	 */
	private void createTrayIcon() {

		log.log(Level.INFO, "トレイアイコンを作成");

		if ( SystemTray.getSystemTray()==null ) {
			return;
		}

		// 「増やす」メニューアイテム
		final MenuItem increaseMenu = new MenuItem("Add one");
		increaseMenu.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				createMascot();
			}
		});

		// 「あつまれ！」メニューアイテム
		final MenuItem gatherMenu = new MenuItem("Gather");
		gatherMenu.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				gatherAll();
			}
		});

		// 「一匹だけ残す」メニューアイテム
		final MenuItem oneMenu = new MenuItem("Reduce to 1");
		oneMenu.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event) {
				remainOne();
			}
		});

		//  Restore IE not implemented yet
		//final MenuItem restoreMenu = new MenuItem("IEを元に戻す");
		//restoreMenu.addActionListener(new ActionListener() {
			//public void actionPerformed(final ActionEvent event) {
				//restoreIE();
			//}
		//});

		// 「ばいばい」メニューアイテム
		final MenuItem closeMenu = new MenuItem("Quit");
		closeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				exit();
			}
		});

		// ポップアップメニューを作成
		final PopupMenu trayPopup = new PopupMenu();
		trayPopup.add(increaseMenu);
		trayPopup.add(gatherMenu);
		trayPopup.add(oneMenu);
//		trayPopup.add(restoreMenu);
		trayPopup.add(new MenuItem("-"));
		trayPopup.add(closeMenu);

		try {
			// トレイアイコンを作成
			final TrayIcon icon = new TrayIcon(ImageIO.read(Main.class.getResource("/icon.png")), "しめじ", trayPopup);
			icon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					// アイコンがダブルクリックされたときも「増える」
					if (SwingUtilities.isLeftMouseButton(e)) {
						createMascot();
					}
				}
			});

			// トレイアイコンを表示
			SystemTray.getSystemTray().add(icon);

		} catch (final IOException e) {
			log.log(Level.SEVERE, "トレイアイコンの作成に失敗", e);
			exit();

		} catch (final AWTException e) {
//			log.log(Level.SEVERE, "トレイアイコンの作成に失敗", e);
			MascotEventHandler.setShowSystemTrayMenu(true);
			getManager().setExitOnLastRemoved(true);
		}

	}

	/**
	 * しめじを一匹作成する.
	 */
	public void createMascot() {

		log.log(Level.INFO, "Creating mascot");

		// マスコットを1個作成
		final Mascot mascot = new Mascot();

		// 範囲外から開始
		mascot.setAnchor(new Point(-1000, -1000));
		// ランダムな向きで
		mascot.setLookRight(Math.random() < 0.5);

		try {
			mascot.setBehavior(getConfiguration().buildBehavior(null, mascot));

			this.getManager().add(mascot);

		} catch (final BehaviorInstantiationException e) {
			log.log(Level.SEVERE, "Cannot set first action", e);
			mascot.dispose();
		} catch (final CantBeAliveException e) {
			log.log(Level.SEVERE, "Cannot stay alive", e);
			mascot.dispose();
		}

	}

	public void gatherAll() {
		Main.this.getManager().setBehaviorAll(Main.this.getConfiguration(), BEHAVIOR_GATHER);
	}

	public void remainOne() {
		Main.this.getManager().remainOne();
	}

	public void restoreIE() {
		NativeFactory.getInstance().getEnvironment().restoreIE();
	}

	public void exit() {

		this.getManager().disposeAll();
		this.getManager().stop();

		System.exit(0);
	}

	public Configuration getConfiguration() {
		return this.configuration;
	}

	private Manager getManager() {
		return this.manager;
	}

}
