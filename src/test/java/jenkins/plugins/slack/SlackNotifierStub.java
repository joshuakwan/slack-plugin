package jenkins.plugins.slack;

public class SlackNotifierStub extends SlackNotifier {

	public SlackNotifierStub(String teamDomain, String authToken, String room,
			String buildServerUrl, String buildServerNick,
			boolean obfuscatorEnabled, String obfuscatorUrl,
			String obfuscatorToken, String sendAs) {
		super(teamDomain, authToken, room, buildServerUrl, buildServerNick,
				obfuscatorEnabled, obfuscatorUrl, obfuscatorToken, sendAs);
	}

	public static class DescriptorImplStub extends SlackNotifier.DescriptorImpl {

		private SlackService slackService;

		@Override
		public synchronized void load() {
		}

		@Override
		SlackService getSlackService(final String teamDomain,
				final String authToken, final String room) {
			return slackService;
		}

		public void setSlackService(SlackService slackService) {
			this.slackService = slackService;
		}
	}
}
