package com.example.tutorial.plugins.impl;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventTypeManager;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;

import com.example.tutorial.plugins.api.MyPluginComponent;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

@ExportAsService({ MyPluginComponent.class })
@Named("myPluginComponent")
public class MyPluginComponentImpl implements MyPluginComponent, InitializingBean, DisposableBean {
	private static final Logger log = Logger.getLogger(MyPluginComponentImpl.class);

	@ComponentImport
	private final EventPublisher eventPublisher;

	@ComponentImport
	private final EventTypeManager eventTypeManager;

	@ComponentImport
	private final AttachmentManager attachmentManager;

	@ComponentImport
	private final ApplicationProperties applicationProperties;

	@Inject
	public MyPluginComponentImpl(final EventPublisher eventPublisher, final EventTypeManager eventTypeManager,
			final AttachmentManager attachmentManager, final ApplicationProperties applicationProperties) {
		log.info("EventPublisher will be initialized!");
		this.eventPublisher = eventPublisher;
		if (null == this.eventPublisher) {
			log.error("EventPublisher has not been injected!");
		}
		log.info("EventTypeManager will be initialized!");
		this.eventTypeManager = eventTypeManager;
		if (null == this.eventTypeManager) {
			log.error("EventTypeManager has not been injected!");
		}
		log.info("AttachmentManager will be initialized!");
		this.attachmentManager = attachmentManager;
		if (null == this.eventTypeManager) {
			log.error("AttachmentManager has not been injected!");
		}
		this.applicationProperties = applicationProperties;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Register CustomEventHandler!");
		if (null != this.eventPublisher) {
			this.eventPublisher.register(this);

		} else {
			log.error("Unable to register EventListener. EventPublisher isn't initialized!");
		}
	}

	@Override
	public void destroy() throws Exception {
		log.info("Remove CustomEventHandler!");
		if (null != this.eventPublisher) {
			this.eventPublisher.unregister(this);
		} else {
			log.error("Unable to unregister EventListener. EventPublisher isn't initialized!");
		}
	}

	public String getName() {
		if (null != applicationProperties) {
			return "JIRA-EventListener-Plugin:" + applicationProperties.getDisplayName();
		}
		return "JIRA-EventListener-Plugin";
	}

	@EventListener
	public void onEvent(IssueEvent event) {
		try {
			log.info("Handle EventType: \"" + eventTypeManager.getEventType(event.getEventTypeId()).getName() + "\"");

			Issue issue = event.getIssue();
			ApplicationUser user = event.getUser();
			String projektName = issue.getProjectObject().getName();
			String statusName = issue.getStatus().getName();

			String message = "Working out event: \"" + user.getName() + " -> " + projektName + " -> " + statusName
					+ " -> " + issue.getKey() + " -> " + eventTypeManager.getEventType(event.getEventTypeId()).getName()
					+ "\"";
			log.info(message);

			try {
				Email email = new Email("foo.to@dummy.de");
				email.setSubject("JIRA-EventListener-Plugin: " + message);
				email.setBody("No further data!");
				ComponentAccessor.getMailServerManager().getDefaultSMTPMailServer().send(email);
			} catch (Exception e) {
				log.error("Error while sending Email", e);
			}

		} catch (Throwable t) {
			log.error("Unable to workout event:", t);
		}
	}
}