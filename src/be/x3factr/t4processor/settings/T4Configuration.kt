package be.x3factr.t4processor.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "T4Configuration", storages = [(Storage("t4plugin.xml"))])
class T4Configuration internal constructor() : PersistentStateComponent<T4Configuration> {
	private var _t4ProcessorPath: String = ""

	var t4ProcessorPath: String
		get() {
			return _t4ProcessorPath;
		}
		set(value) {
			_t4ProcessorPath = value;
		}

	override fun getState(): T4Configuration? {
		return this
	}

	override fun loadState(wcfToolingConfiguration: T4Configuration) {
		XmlSerializerUtil.copyBean(wcfToolingConfiguration, this)
	}

	companion object {
		val instance: T4Configuration
			get() = ServiceManager.getService(T4Configuration::class.java)
	}
}