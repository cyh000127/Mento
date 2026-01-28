package com.mento.domain.notification.event;

import org.springframework.context.ApplicationEvent;

import com.mento.domain.notification.entity.Notification;

import lombok.Getter;

@Getter
public class NotificationEvent extends ApplicationEvent {

	private final Notification notification;

	public NotificationEvent(Object source, Notification notification) {
		super(source);
		this.notification = notification;
	}
}
