package io.nais

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.NotificationType.ERROR
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
class NAISConfigAction: AnAction() {
   override fun actionPerformed(event: AnActionEvent) {
      val project = event.project!!
      try {
         val projectDir = project.basePath ?: throw RuntimeException("Unable to determine project directory")
         val platform = determinePlatform(projectDir) ?: throw RuntimeException("Unable to determine project type from files in $projectDir")
         InputDialog(project, platform, ExtraFeature.values().toList()).also { dialog ->
            if(dialog.showAndGet()) {
               writeAppConfig(requestAppConfig(dialog.formData()), projectDir)
               notify("Files written to $projectDir", NotificationType.INFORMATION, project)
            }
         }
      } catch (ex: Exception) {
         notify(ex.message?: "unknown error", ERROR, project)
      }
   }
}

enum class ExtraFeature(val displayName: String, val shortName: String) {
   POSTGRES("Postgres", "postgres"),
   IDPORTEN("ID-porten", "idporten"),
   AAD("Azure AD", "aad")
}

private fun notify(msg: String, type: NotificationType, project: Project) =
   NotificationGroupManager.getInstance().getNotificationGroup("NAIS")
      .createNotification(msg, type)
      .notify(project)
