package be.x3factr.t4processor.actions

import be.x3factr.t4processor.infrastructure.Translations
import be.x3factr.t4processor.settings.T4Configuration
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint
import com.jetbrains.rider.model.SolutionAnalysisAction
import com.jetbrains.rider.model.solutionAnalysisModel
import com.jetbrains.rider.projectView.solution
import com.jetbrains.rider.runtime.RiderDotNetActiveRuntimeHost
import com.jetbrains.rider.solutionAnalysis.SolutionAnalysisHost
import com.jetbrains.rider.util.idea.application
import com.jetbrains.rider.util.idea.getComponent
import javafx.application.Application
import java.util.stream.Collectors

class T4Processor : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        application.saveAll()

        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val filePath = file.path

        saveAndRefreshTranslationFiles(file)

        val settings = T4Configuration.instance

        val gc = GeneralCommandLine(settings.t4ProcessorPath, filePath)
        val dotNetRuntime = event.project?.getComponent<RiderDotNetActiveRuntimeHost>(RiderDotNetActiveRuntimeHost::class.java)
        if (dotNetRuntime == null) {
            Messages.showErrorDialog(Translations.MonoRuntimeNotFoundError, Translations.ProcessingErrorTitle)
        }
        dotNetRuntime?.getCurrentDotNetRuntime(true)?.getRuntimeOrThrow()?.patchRunCommandLine(gc, emptyList())

        val proc = gc.withRedirectErrorStream(true).createProcess()
        proc.waitFor()

        // region outputStreams
        val outputOutput = proc.inputStream.bufferedReader().lines().collect(Collectors.toList())
        val errorOutput = proc.errorStream.bufferedReader().lines().collect(Collectors.toList())
        // endregion

        when (proc.exitValue()) {
            0 -> {
                saveAndRefreshTranslationFiles(file)

                event.project?.solution?.solutionAnalysisModel?.runAction?.fire(SolutionAnalysisAction.ReanalyzeAll)

                application.saveAll()

                builder.setFadeoutTime(1500)
                        .createBalloon()
                        .show(RelativePoint.getNorthEastOf(WindowManager.getInstance().getIdeFrame(event.project).component), Balloon.Position.above)
            }
            1 -> Messages.showErrorDialog(errorOutput.toString(), Translations.ProcessingErrorTitle)
            else -> Messages.showErrorDialog(outputOutput.toString(), Translations.ProcessingErrorTitle)
        }
    }

    override fun update(event: AnActionEvent) {
        super.update(event)

        val file = event.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val visible = file.name.endsWith(".tt")
        event.presentation.isEnabledAndVisible = visible
    }

    private fun saveAndRefreshTranslationFiles(file: VirtualFile) {
        for (child in file.parent.children) {
            child.refresh(false, true)
        }
    }

    companion object {
        private val factory = JBPopupFactory.getInstance()
        private val builder = factory.createHtmlTextBalloonBuilder(Translations.CommandFinishedBalloonText, MessageType.INFO, null)
    }
}