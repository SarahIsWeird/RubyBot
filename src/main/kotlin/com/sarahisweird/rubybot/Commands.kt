package com.sarahisweird.rubybot

import com.sarahisweird.rubybot.jni.CGKeyCode
import com.sarahisweird.rubybot.jni.JNIKeyPress
import com.sksamuel.scrimage.ImmutableImage
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.ComponentInteraction
import dev.kord.core.event.interaction.InteractionCreateEvent
import dev.kord.rest.builder.component.ActionRowBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dev.kord.x.emoji.Emojis
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.button
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.concurrent.thread

val keyPress = JNIKeyPress()

var shouldContinue = true

@OptIn(KordPreview::class)
fun rubyCommands() = commands("Ruby") {
    command("game") {
        execute {
            shouldContinue = true

            thread {
                runBlocking {
                    var lastMessage: Message? = null

                    while (shouldContinue) {
                        val newMessage = channel.createMessage {
                            val thread = addScreenshot()

                            actionRow { buildFirstRow() }
                            actionRow { buildSecondRow() }
                            actionRow { buildThirdRow() }

                            thread.join()
                        }

                        lastMessage?.delete()

                        lastMessage = newMessage

                        delay(5000)
                    }
                }
            }
        }
    }
}

@OptIn(KordPreview::class)
fun MessageCreateBuilder.addButtons() {
    actionRow { buildFirstRow() }
    actionRow { buildSecondRow() }
    actionRow { buildThirdRow() }
}

@OptIn(KordPreview::class)
fun ActionRowBuilder.buildFirstRow() {
    button(null, Emojis.regionalIndicatorL) {
        customId = "rb-controls-l"
    }

    button(null, Emojis.a) {
        customId = "rb-controls-a"
    }

    button(null, Emojis.arrowUp) {
        customId = "rb-controls-up"
    }

    button(null, Emojis.b) {
        customId = "rb-controls-b"
    }

    button(null, Emojis.regionalIndicatorR) {
        customId = "rb-controls-r"
    }
}

@OptIn(KordPreview::class)
fun ActionRowBuilder.buildSecondRow() {
    button(null, Emojis.arrowForward) {
        customId = "rb-controls-start"
    }

    button(null, Emojis.arrowLeft) {
        customId = "rb-controls-left"
    }

    button(null, Emojis.arrowDown) {
        customId = "rb-controls-down"
    }

    button(null, Emojis.arrowRight) {
        customId = "rb-controls-right"
    }

    button(null, Emojis.recordButton) {
        customId = "rb-controls-select"
    }
}

@OptIn(KordPreview::class)
fun ActionRowBuilder.buildThirdRow() {
    button(null, Emojis.stopButton) {
        customId = "rb-controls-stop"
    }
}

@OptIn(KordPreview::class)
fun rubyListener() = listeners {
    on<InteractionCreateEvent> {
        if (!shouldContinue) return@on
        val ci = interaction as? ComponentInteraction ?: return@on
        if (!ci.componentId.startsWith("rb-controls-")) return@on

        val id = ci.componentId.drop("rb-controls-".length)

        ci.acknowledgePublic()

        when {
            (id == "l") -> keyPress.stroke(CGKeyCode.Q)
            (id == "a") -> keyPress.stroke(CGKeyCode.A)
            (id == "up") -> keyPress.stroke(CGKeyCode.ARROW_UP)
            (id == "b") -> keyPress.stroke(CGKeyCode.B)
            (id == "r") -> keyPress.stroke(CGKeyCode.E)

            (id == "start") -> keyPress.stroke(CGKeyCode.S)
            (id == "left") -> keyPress.stroke(CGKeyCode.ARROW_LEFT)
            (id == "down") -> keyPress.stroke(CGKeyCode.ARROW_DOWN)
            (id == "right") -> keyPress.stroke(CGKeyCode.ARROW_RIGHT)
            (id == "select") -> keyPress.stroke(CGKeyCode.SHIFT)

            (id == "stop") -> shouldContinue = false
        }
    }
}

suspend fun MessageCreateBuilder.addScreenshot() = thread {
    val data = keyPress.getDisplayContent(keyPress.mainDisplayHandle)
        .map(Byte::toInt).toIntArray()
    val width = keyPress.lastWidth.toInt()
    val height = keyPress.lastHeight.toInt()

    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE)
    bufferedImage.getWritableTile(0, 0)
        .setPixels(0, 0, width, height, data)

    val image = ImmutableImage.fromAwt(bufferedImage).map { pixel ->
        return@map Color(
            pixel.blue(),
            pixel.green(),
            pixel.red(),
            pixel.alpha()
        )
    }

    val file = File("foo.png")
    ImageIO.write(image.awt(), "png", file)

    runBlocking {
        addFile(file.toPath())
    }
}