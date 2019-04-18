package gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import background.BackgroundUtilities;
import background.BitSearchBot;
import background.OAuth2Client;
import background.OAuthConstants;
import background.util.FileIOUtils;
import background.util.FormatUtil;
import irc.message.MessageHandler;

public class ApplicationGui extends JFrame
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3320657702299522826L;
	private static boolean m_bSearching = true;
//	private static final JTextArea m_jtaAPIKey = new JTextArea();
	private static final JLabel m_jlTotalAmount = new JLabel("$0.00");
	private static String m_strFileName = "";
	private static final JTextArea m_jtaAdjustmentAmt = new JTextArea();
	private static final JLabel m_jlInfoText = new JLabel("Test text");
	private static BigDecimal m_bdTopDonation = BigDecimal.ZERO;
	 private static final JTextArea m_jtaUsername = new JTextArea("Enter username");
	// before connecting");
	private static BitSearchBot m_bot = null;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static DataStoreFactory DATA_STORE_FACTORY;
	private static String ACCESS_TOKEN;
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".store/donation_tracker");
	private static JLabel m_jlRecentSupporter;
	private static JLabel m_jlTopSupporter;

	public static void main(String args[])
	{

		updateSevenTotalAmount(
				String.valueOf(FormatUtil.getFormattedDollarAmount(FileIOUtils.getStoredValueForLabel("Total:"))));
		try
		{
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
			final Credential cred = BackgroundUtilities.authorize(DATA_STORE_FACTORY);

			HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer()
			{
				@Override
				public void initialize(HttpRequest request) throws IOException
				{
					cred.initialize(request);
					request.setParser(new JsonObjectParser(JSON_FACTORY));
				}
			});
			ACCESS_TOKEN = cred.getAccessToken();
//			searchDonations(requestFactory);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//		JLabel jlAPIKey = new JLabel("Twitch Alerts API Token:");
//		jlAPIKey.setBounds(0, 5, 160, 20);
//		jFrame.add(jlAPIKey);

//		m_jtaAPIKey.setBounds(165, 5, 180, 20);
//		m_jtaAPIKey.setRows(1);
//		jFrame.add(m_jtaAPIKey);

		JButton jbConnect = new JButton("Connect to Chat");
		jbConnect.setBounds(0, 45, 140, 30);
		jbConnect.addActionListener(getActionListenerConnect());
		jFrame.add(jbConnect);

		 m_jtaUsername.setBounds(165, 45, 195, 20);
		 m_jtaUsername.setRows(1);
		 jFrame.add(m_jtaUsername);

		JButton jbReconnect = new JButton("Reconnect");
		jbReconnect.setBounds(0, 80, 100, 30);
		jbReconnect.addActionListener(getActionListenerReconnect());
		jFrame.add(jbReconnect);

		JButton jbRun = new JButton("Run");
		jbRun.setBounds(0, 120, 60, 30);
		jbRun.addActionListener(getActionListenerSearch());
		jFrame.add(jbRun);

		JButton jbStop = new JButton("Stop");
		jbStop.setBounds(70, 120, 60, 30);
		jbStop.addActionListener(getActionListenerStop());
		jFrame.add(jbStop);

		JLabel jlSevenKarmaText = new JLabel("Total:");
		jlSevenKarmaText.setBounds(0, 150, 200, 20);
		jFrame.add(jlSevenKarmaText);

		m_jlTotalAmount.setBounds(125, 150, 160, 20);
		jFrame.add(m_jlTotalAmount);

		m_jlRecentSupporter = new JLabel("Recent Supporter: ");
		m_jlRecentSupporter.setBounds(0, 170, 275, 20);
		jFrame.add(m_jlRecentSupporter);
		
		m_jlTopSupporter = new JLabel("Top Supporter: ");
		m_jlTopSupporter.setBounds(0, 190, 275, 20);
		jFrame.add(m_jlTopSupporter);

		JLabel jlAdjustment = new JLabel("Adjustments:");
		jlAdjustment.setBounds(0, 225, 80, 20);
		jFrame.add(jlAdjustment);

		m_jtaAdjustmentAmt.setBounds(85, 225, 40, 20);
		jFrame.add(m_jtaAdjustmentAmt);

		JButton jbAdjustment = new JButton("Make Adjustment");
		jbAdjustment.setBounds(0, 250, 140, 20);
		jbAdjustment.addActionListener(getActionAdjustment());

		jFrame.add(jbAdjustment);

		JButton jbReset = new JButton("Reset Top/Recent Supporter");
		jbReset.setBounds(0, 290, 200, 40);
		jbReset.addActionListener(getActionReset());

		jFrame.add(jbReset);

		JLabel jlInfo = new JLabel("Info");
		jlInfo.setBounds(0, 330, 50, 20);
		jFrame.add(jlInfo);

		m_jlInfoText.setBounds(0, 390, 300, 50);
		m_jlInfoText.setBackground(Color.BLACK);
		m_jlInfoText.setForeground(Color.GREEN);
		m_jlInfoText.setOpaque(true);
		m_jlInfoText.setVerticalAlignment(JLabel.TOP);
		jFrame.add(m_jlInfoText);

		// m_jfcChangeSound.setBounds(0, 195, 140, 80);
		// jFrame.add(m_jfcChangeSound);

		jFrame.setSize(400, 500);
		jFrame.setLayout(null);
		jFrame.setVisible(true);

		setTopDonation(BigDecimal.ZERO, " ", " ");
		FileIOUtils.initializeRecentFile();

	}

	private static void searchDonations(HttpRequestFactory p_factory)
	{

	}

	private static ActionListener getActionListenerSearch()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Runnable r = new Runnable()
				{

					@Override
					public void run()
					{
						try
						{
							// final Credential cred = authorize();
							while (m_bSearching)
							{
								try
								{
									OAuth2Client.searchServerUpdates(ACCESS_TOKEN, m_strFileName);
									Thread.sleep(3000);
								}
								catch (Throwable t)
								{
									if (OAuthConstants.DEBUGGING)
									{
										t.printStackTrace();
									}
								}
							}
						}
						catch (Exception e)
						{
							if (OAuthConstants.DEBUGGING)
							{
								e.printStackTrace();
							}
						}
						setInfoText("Stopped");

					}
				};
				new Thread(r).start();
			}
		};
	}

	private static ActionListener getActionReset()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Runnable r = new Runnable()
				{

					@Override
					public void run()
					{

						FileIOUtils.initializeRecentFile();
						setTopDonation(BigDecimal.ZERO, " ", " ");

					}
				};
				new Thread(r).start();
			}
		};
	}

	private static ActionListener getActionListenerConnect()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				try
				{
					MessageHandler handler = new MessageHandler()
					{
						@Override
						public void onCheer(String channel, String sender, int amount, String message)
						{
							double f = (double) amount / 100;
							BigDecimal bdAmount = BigDecimal.valueOf(f);
							OAuth2Client.makeAdjustment(bdAmount, m_strFileName);
							if (getTopDonation().compareTo(bdAmount) == -1)
							{
								setTopDonation(bdAmount, sender, OAuthConstants.BITS);
							}

								FileIOUtils.updateRecentDonator(sender, BigDecimal.valueOf(amount), OAuthConstants.BITS);
						}
					};
					m_bot = new BitSearchBot(handler);
					m_bot.setVerbose(true);
					m_bot.setNick("botofseph");
					m_bot.setPassword("oauth:t0l9tdo9s5buntdxw7j0dqax35nvgn");
					m_bot.connect();
					// bot.connect("irc.chat.twitch.tv", 6667,
					// "oauth:ca3ww72dfyhq39yea6pfn6oktaee0o");
					 m_bot.joinChannel("#" + m_jtaUsername.getText().toLowerCase());
				}
				catch (Throwable t)
				{
					if (OAuthConstants.DEBUGGING)
					{
						t.printStackTrace();
					}
				}

			}
		};
	}

	private static ActionListener getActionListenerReconnect()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{

				if (m_bot != null && !m_bot.isConnected())
				{
					m_bot.connect();
				}
			}
		};
	}

	private static ActionListener getActionListenerStop()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				m_bSearching = false;
				setInfoText("Stopping...");
			}
		};
	}

	private static ActionListener getActionAdjustment()
	{
		return new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				BigDecimal bdAdjustmentAmt = new BigDecimal(
						m_jtaAdjustmentAmt.getText().replaceAll(",", "").replaceAll("$", "").replaceAll(" ", "").trim());
				OAuth2Client.makeAdjustment(bdAdjustmentAmt, m_strFileName);

			}
		};
	}

	public static void updateSevenTotalAmount(String p_strValue)
	{
		m_jlTotalAmount.setText(p_strValue);
	}

	public static void setInfoText(String p_strInfoText)
	{
		m_jlInfoText.setText(p_strInfoText);
	}

	public void onMessage(String p_strChannel, String p_strSender, String p_strLogin, String p_strHostname,
			String p_strMessage)
	{
		if (OAuthConstants.DEBUGGING)
		{
			System.out.println(p_strMessage);
		}
	}

	public static BigDecimal getTopDonation()
	{
		return m_bdTopDonation;
	}

	public static void setTopDonation(BigDecimal p_bdTopDonation, String p_strUsername, String p_strType)
	{
		m_bdTopDonation = p_bdTopDonation;
		FileIOUtils.updateTopDonator(p_strUsername, p_bdTopDonation, p_strType);
	}
	
	public static void updateTopDonation(String p_strMessage)
	{
		m_jlTopSupporter.setText("Top Supporter: " + p_strMessage);
	}
	
	public static void updateRecentDonation(String p_strMessage)
	{
		m_jlRecentSupporter.setText("Recent Supporter: " + p_strMessage);
	}

}
