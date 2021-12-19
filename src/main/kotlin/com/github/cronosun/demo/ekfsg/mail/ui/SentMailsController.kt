package com.github.cronosun.demo.ekfsg.mail.ui

import com.github.cronosun.demo.ekfsg.mail.MailService
import com.github.cronosun.demo.ekfsg.mail.SentMail
import io.reactivex.rxjava3.core.Observable

class SentMailsController(
    private val mailService: MailService
) {
    val sentMails: Observable<List<SentMail>> = Observable.create {
        val mails = mailService.listSentMailsOrderedBySentDateDescending(100)
        it.onNext(mails)
    }
}