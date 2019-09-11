package be.x3factr.t4processor.settings

import be.x3factr.t4processor.infrastructure.Translations
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.IdeBorderFactory
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import org.apache.commons.lang.SystemUtils
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class T4ConfigurableUI(private val config: T4Configuration) {
    private val rootPanel: JPanel = JPanel().apply {
        layout = GridLayoutManager(2, 2)
        border = IdeBorderFactory.createTitledBorder(Translations.SettingsTitle, true)
    }

    private val t4ProcessorPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    fun getPanel(): JComponent {
        return rootPanel
    }

    val isModified: Boolean
        get() = t4ProcessorPathField.text != config.t4ProcessorPath

    fun apply() {
        config.t4ProcessorPath = t4ProcessorPathField.text
    }

    fun reset() {
        t4ProcessorPathField.text = config.t4ProcessorPath
    }

    init {
        // Build UI
        rootPanel.add(JLabel().apply {
            text = Translations.SettingT4ProcessorPathLabel
            labelFor = t4ProcessorPathField
        }, GridConstraints().apply {
            row = 0
            column = 0
            vSizePolicy = 0
            hSizePolicy = 0
            anchor = GridConstraints.ANCHOR_WEST
        })

        rootPanel.add(t4ProcessorPathField.apply {
            isEditable = true
            isEnabled = true
            addActionListener({
                val descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor("exe")
                        .withFileFilter({ it != null && it.name.toLowerCase() == "texttransform.exe" })
                descriptor.title = "Browse for TextTransform executable..."

                val file = FileChooser.chooseFile(descriptor, null, LocalFileSystem.getInstance().findFileByPath(t4ProcessorPathField.text))
                if (file != null) {
                    t4ProcessorPathField.text = file.path
                    if (SystemUtils.IS_OS_WINDOWS) {
                        t4ProcessorPathField.text = t4ProcessorPathField.text.replace('/', '\\')
                    }
                }
            })
        }, GridConstraints().apply {
            row = 0
            column = 1
            fill = GridConstraints.FILL_HORIZONTAL
            vSizePolicy = 0
            hSizePolicy = 6
            anchor = GridConstraints.ANCHOR_NORTHWEST
        })

        rootPanel.add(Spacer(), GridConstraints().apply {
            row = 1
            column = 0
            colSpan = 2
            anchor = GridConstraints.ANCHOR_SOUTH
        })
    }
}