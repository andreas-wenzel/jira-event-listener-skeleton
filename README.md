# jira-event-listener-skeleton

Voraussetzung zum Bauen ist ein sauber installiertes atlas-sdk.

Das Plugin wurde auf Basis des Altassian Tutorials "[Tutorial - Writing JIRA event listeners with the atlassian-event library](https://developer.atlassian.com/jiradev/jira-platform/guides/other/tutorial-writing-jira-event-listeners-with-the-atlassian-event-library)" erstellt und anschließend so angepasst, dass es in Version 7.1.2 von JIRA auch wirklich läuft.

```java
@EventListener
public void onEvent(IssueEvent event) {
	try {
		/* 
		 * Do something! 
		 */
		log.info("Handle EventType: \"" + eventTypeManager.getEventType(event.getEventTypeId()).getName() + "\"");
	} catch (Throwable t) {
		log.error("Unable to workout event:", t);
	}
}
```


