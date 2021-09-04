package com.sarahisweird.rubybot

import dev.kord.common.annotation.KordPreview
import me.jakejmattson.discordkt.api.dsl.bot

@OptIn(KordPreview::class)
fun main() {
    val token = System.getenv("rubybot_token")

    bot(token) {
        prefix { "rb " }
    }
}