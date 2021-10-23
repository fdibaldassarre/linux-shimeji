package com.group_finity.mascot.behavior;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import com.group_finity.mascot.Mascot;
import com.group_finity.mascot.action.Action;
import com.group_finity.mascot.config.BehaviourName;
import com.group_finity.mascot.config.Configuration;
import com.group_finity.mascot.config.KnownBehaviour;
import com.group_finity.mascot.environment.MascotEnvironment;
import com.group_finity.mascot.exception.BehaviorInstantiationException;
import com.group_finity.mascot.exception.CantBeAliveException;
import com.group_finity.mascot.exception.LostGroundException;
import com.group_finity.mascot.exception.VariableException;

/**
 * サンプル用の単純な振る舞い.
 */
public class UserBehavior implements Behavior {
	private static final Logger log = Logger.getLogger(UserBehavior.class.getName());

	private final BehaviourName name;

	private final Configuration configuration;

	private final Action action;

	private Mascot mascot;

	public UserBehavior(final BehaviourName name, final Action action, final Configuration configuration) {
		this.name = name;
		this.configuration = configuration;
		this.action = action;
	}

	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public synchronized void init(final Mascot mascot) throws CantBeAliveException {

		this.setMascot(mascot);

		log.log(Level.INFO, "Action begins({0},{1})", new Object[] { this.getMascot(), this });

		try {
			getAction().init(mascot);
			if (!getAction().hasNext()) {
				try {
					mascot.setBehavior(this.getConfiguration().buildBehavior(name, mascot));
				} catch (final BehaviorInstantiationException e) {
					throw new CantBeAliveException("Failed to initialize the next action", e);
				}
			}
		} catch (final VariableException e) {
			throw new CantBeAliveException("An error occurred during variable evaluation", e);
		}

	}

	private Configuration getConfiguration() {
		return this.configuration;
	}

	private Action getAction() {
		return this.action;
	}

	/**
	 * マウスが押された.
	 * 左ボタンだったらドラッグ開始.
	 * @throws CantBeAliveException 
	 */
	public synchronized void mousePressed(final MouseEvent event) throws CantBeAliveException {

		if (SwingUtilities.isLeftMouseButton(event)) {
			// ドラッグ開始のお知らせ
			try {
				getMascot().setBehavior(this.getConfiguration().buildBehavior(KnownBehaviour.Dragged));
			} catch (final BehaviorInstantiationException e) {
				throw new CantBeAliveException("Cannot initialize Dragged behaviour", e);
			}
		}

	}

	/**
	 * マウスが離れた.
	 * 左ボタンだったらドラッグ終了.
	 * @throws CantBeAliveException 
	 */
	public synchronized void mouseReleased(final MouseEvent event) throws CantBeAliveException {

		if (SwingUtilities.isLeftMouseButton(event)) {
			// ドラッグ終了のお知らせ
			try {
				getMascot().setBehavior(this.getConfiguration().buildBehavior(KnownBehaviour.Thrown));
			} catch (final BehaviorInstantiationException e) {
				throw new CantBeAliveException("Cannot initialize Thrown behaviour", e);
			}
		}

	}

	@Override
	public synchronized void next() throws CantBeAliveException {

		try {
			if (getAction().hasNext()) {
				getAction().next();
			}

			if (getAction().hasNext()) {

				// / IE をちゃんと持てているかチェック
				if ((getMascot().getBounds().getX() + getMascot().getBounds().getWidth() <= getEnvironment().getScreen()
						.getLeft())
						|| (getEnvironment().getScreen().getRight() <= getMascot().getBounds().getX())
						|| (getEnvironment().getScreen().getBottom() <= getMascot().getBounds().getY())) {

					log.log(Level.INFO, "Got out of screen({0},{1})", new Object[] { getMascot(), this });

					getMascot().setAnchor(
							new Point((int) (Math.random() * (getEnvironment().getScreen().getRight() - getEnvironment()
									.getScreen().getLeft()))
									+ getEnvironment().getScreen().getLeft(), getEnvironment().getScreen().getTop() - 256));

					try {
						getMascot().setBehavior(this.getConfiguration().buildBehavior(KnownBehaviour.Fall));
					} catch (final BehaviorInstantiationException e) {
						throw new CantBeAliveException("Cannot initialize Fall behaviour", e);
					}
				}

			} else {
				log.log(Level.INFO, "Action is over({0},{1})", new Object[] { getMascot(), this });

				try {
					getMascot().setBehavior(this.getConfiguration().buildBehavior(name, getMascot()));
				} catch (final BehaviorInstantiationException e) {
					throw new CantBeAliveException("Failed to initialize the next action", e);
				}
			}
		} catch (final LostGroundException e) {
			log.log(Level.INFO, "Off the ground({0},{1})", new Object[] { getMascot(), this });

			try {
				getMascot().setBehavior(this.getConfiguration().buildBehavior(KnownBehaviour.Fall));
			} catch (final BehaviorInstantiationException ex) {
				throw new CantBeAliveException("Cannot initialize Fall behaviour", ex);
			}
		} catch (final VariableException e) {
			throw new CantBeAliveException("An error occurred during variable evaluation", e);
		}

	}

	private void setMascot(final Mascot mascot) {
		this.mascot = mascot;
	}

	private Mascot getMascot() {
		return this.mascot;
	}

	protected MascotEnvironment getEnvironment() {
		return getMascot().getEnvironment();
	}
}
