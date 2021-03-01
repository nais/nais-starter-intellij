# nais-starter-intellij

```NAIS is the application platform for the Norwegian Welfare Administration (NAV). This plugin is probably not very useful to you if you don't work there.```

This plugin lets [NAIS](https://nais.io) users bootstrap build and deploy for their app without doing lots of manual YAML gymnastics. 

## Features

This plugin asks for the informations it needs, uses this info to retrieve basic YAML from [start.nais.io](https://start.nais.io) and saves it in the project directory.

Currently supported project types are:
- Maven
- Gradle
- NodeJS/NPM

Project type is determined automatically based on the presence of `pom.xml`, `package.json` et al. in the project directory

## Requirements

No external dependencies or other prerequisites needed.

## Extension Settings

No config needed

## Test it on your own computer

Open this project in Intellij IDEA and choose `Run -> Run 'Plugin'`

## Questions? Comments? Issues?

Raise an issue in this repo. Internal users kan contact us on Slack in the #nais channel.

