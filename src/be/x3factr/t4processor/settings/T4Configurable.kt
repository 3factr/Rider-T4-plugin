package be.x3factr.t4processor.settings

import be.x3factr.t4processor.infrastructure.Translations
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.SearchableConfigurable
import org.jetbrains.annotations.Nls

import javax.swing.*


class T4Configurable : SearchableConfigurable {
	private var ui: T4ConfigurableUI? = null
	private val config: T4Configuration = T4Configuration.instance

	@Nls
	override fun getDisplayName(): String {
		return Translations.SettingsTitle
	}

	override fun getHelpTopic(): String? {
		return "preference.T4Configurable"
	}

	override fun getId(): String {
		return "preference.T4Configurable"
	}

	override fun enableSearch(option: String?): Runnable? {
		return null
	}

	override fun createComponent(): JComponent? {
		ui = T4ConfigurableUI(config)
		return ui!!.getPanel()
	}

	override fun isModified(): Boolean {
		return ui!!.isModified
	}

	@Throws(ConfigurationException::class)
	override fun apply() {
		ui!!.apply()
	}

	override fun reset() {
		ui!!.reset()
	}

	override fun disposeUIResources() {
		ui = null
	}
}