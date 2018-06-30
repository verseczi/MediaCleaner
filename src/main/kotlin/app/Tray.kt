/*package com.mediacleaner.app

import com.mediacleaner.Config
import com.mediacleaner.view.SettingsView
import javafx.application.Platform
import javafx.stage.Stage
import mu.KotlinLogging
import tornadofx.*
import java.util.*

class Tray: App(SettingsView::class) {
    private val logger = KotlinLogging.logger {}
    private var settings = Config().getSettings()
    private var timer = Timer()
    var mediaCleaner = MediaCleaner()

    override fun start(stage: Stage) {
        super.start(stage)
        val settingsView = find(SettingsView::class)

        fun startSettingsView(): Boolean{
            val modal = settingsView.apply { openModal(block = true) }
            if(settingsView.save) {
                val result = modal.getResult()
                if(settings.interval != result.interval) {
                    timer.cancel()
                    mediaCleaner.startTimer(mediaCleaner)
                }

                Config().saveSettings(result)
                settings = result
                println(result)
            } else
                println("exit")

            settingsView.save = false

            return true
        }

        trayicon(resources.stream("/Logo3.png")) {
            setOnMouseClicked(fxThread = true) {
                startSettingsView()
            }

            menu("MyApp") {
                item("Start...") {
                    setOnAction(fxThread = true) {
                        logger.info("Teszt")
                        mediaCleaner.startTimer(mediaCleaner)
                    }
                }
                item("Stop timer...") {
                    setOnAction(fxThread = true) {
                        mediaCleaner.stopTimer()
                    }
                }
                item("Exit") {
                    setOnAction(fxThread = true) {
                        FX.primaryStage.close()
                        Platform.exit()
                    }
                }
            }
        }
    }

    class MediaCleanerTimer(mediaCleaner: MediaCleaner): TimerTask() {
        private val mediaCleaner = mediaCleaner
        override fun run() {
            mediaCleaner.mediaCleaner()
            if(mediaCleaner.cancelled) {
                mediaCleaner.startTimer(mediaCleaner)
            }
        }
    }
}*/