package decaf.dialog;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import decaf.event.EventService;
import decaf.gui.User;
import decaf.messaging.outboundevent.OutboundEvent;

public class QuestionDialog extends JDialog {
	private JTextField textField = new JTextField(20);

	private JButton sendButton = new JButton(new AbstractAction("Ask") {
		public void actionPerformed(ActionEvent e) {
			if (textField.getText() != null
					&& textField.getText().length() != 0) {
				if (User.getInstance().isGuest()) {
					EventService.getInstance().publish(
							new OutboundEvent("tell 4 [Decaf] "
									+ textField.getText()));
				} else {
					EventService.getInstance().publish(
							new OutboundEvent("tell 1 [Decaf] "
									+ textField.getText()));
				}
			}
			setVisible(false);
		}
	});

	public QuestionDialog() {
		super();
		setTitle("Ask A Question");
		getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		add(new JLabel("Question:"));
		add(textField);
		add(sendButton);
		pack();
	}
}
