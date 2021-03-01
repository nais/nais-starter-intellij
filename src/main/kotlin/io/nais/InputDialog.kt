package io.nais

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.LINE_START
import java.awt.GridBagLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*

class FeatureCheckbox(val feature: ExtraFeature): JCheckBox(feature.displayName)

data class FormData(val appName: String, val team: String, val platform: String, val extras: List<String>)

class InputDialog(private val project: Project, projectType: String, extras: List<ExtraFeature>) : DialogWrapper(project, false), KeyListener {
   private val txtWidth = 19
   private val appLabel = JLabel("App")
   private val appField = JTextField(project.name, txtWidth)
   private val teamLabel = JLabel("Team")
   private val teamField = JTextField(txtWidth)
   private val platformLabel = JLabel("Platform")
   private val platformField = JTextField(projectType, txtWidth).apply { isEnabled = false }
   private val extrasCheckers = extras.map { FeatureCheckbox(it) }

   init {
      init()
      title = "NAISify ${project.name}"
   }

   override fun createCenterPanel() = JPanel().apply {
      title = "NAISify ${project.name}"
      layout = GridBagLayout()
      val gbConstraints = GridBagConstraints().apply {
         gridx = 0
         gridy = 0
         gridwidth = 1
         gridheight = 1
         ipadx = 5
         ipady = 5
         anchor = LINE_START
      }

      appLabel.labelFor = appField
      add(appLabel, gbConstraints)
      gbConstraints.gridx = 1
      add(appField, gbConstraints)

      teamLabel.labelFor = teamField
      gbConstraints.gridx = 0
      gbConstraints.gridy = 1
      add(teamLabel, gbConstraints)
      gbConstraints.gridx = 1
      add(teamField, gbConstraints)

      platformLabel.labelFor = platformField
      gbConstraints.gridx = 0
      gbConstraints.gridy = 2
      add(platformLabel, gbConstraints)
      gbConstraints.gridx = 1
      add(platformField, gbConstraints)

      gbConstraints.gridx = 0
      gbConstraints.gridy = 3
      gbConstraints.gridwidth = 2
      add(JPanel().apply {
         border = BorderFactory.createTitledBorder("Extras")
         extrasCheckers.forEach(::add)
      }, gbConstraints)

      listOf(appField, teamField).forEach { it.addKeyListener(this@InputDialog) }

      okAction.isEnabled = false
      teamField.grabFocus()
   }

   fun inputIsValid() =
      listOf(appField.text, teamField.text).none{ it.trim().isEmpty() }

   fun formData() = FormData(
      appField.text,
      teamField.text,
      platformField.text,
      extrasCheckers.filter{ it.isSelected }.map { it.feature.shortName }
   )

   override fun keyTyped(e: KeyEvent?) { /* noop */ }
   override fun keyPressed(e: KeyEvent?) { /* noop */ }
   override fun keyReleased(e: KeyEvent?) {
      okAction.isEnabled = inputIsValid()
   }

}


