# RSX Language Support for IntelliJ IDEA

![Build](https://github.com/guobin211/rsx-idea/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
RSX Language Support provides comprehensive IDE features for RSX (Rust + TypeScript + Template + SCSS) files.

## Features

- **Syntax Highlighting**: Full syntax highlighting for all four RSX sections
  - Rust code block (`---...---`)
  - TypeScript/Script block (`<script>...</script>`)
  - HTML Template block (`<template>...</template>`)
  - SCSS/Style block (`<style>...</style>`)

- **Template Directives**: Support for RSX template directives
  - Conditional: `{{@if}}`, `{{:else if}}`, `{{:elseif}}`, `{{:else}}`, `{{/if}}`
  - Loop: `{{@each items as item, index}}`, `{{/each}}`
  - Raw HTML: `{{@html content}}`

- **Text Interpolation**: `{{ expression }}` with expression parsing
  - Property access: `{{ user.name }}`
  - Function calls: `{{ formatDate(date) }}`
  - Binary expressions: `{{ a > b && c < d }}`
  - Ternary expressions: `{{ condition ? 'yes' : 'no' }}`

- **Code Folding**: Collapse sections and directive blocks

- **Bracket Matching**: Auto-matching for `{{ }}`, `( )`, `[ ]`, `< >`

- **Code Completion**: HTML tags, attributes, and directives

- **Commenter**: HTML-style comments (`<!-- ... -->`)
<!-- Plugin description end -->

## Installation

- Using the IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "RSX Language Support"</kbd> >
  <kbd>Install</kbd>

- Manually:

  Download the [latest release](https://github.com/guobin211/rsx-idea/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## RSX File Structure

```rsx
---
// Rust section: server-side logic
use rsx::{Request, Response};

async fn get_server_side_props(req: Request) -> Response {
    Response::json!({})
}
---

<script>
// TypeScript section: client-side logic
const { data } = defineProps<{ data: any }>()
</script>

<template>
<!-- Template section: Handlebars-like syntax -->
<div>{{ data }}</div>
{{@if condition}}
    <p>Conditional content</p>
{{/if}}
</template>

<style>
/* Style section: SCSS */
div { padding: 20px; }
</style>
```

## Development

### Build

```bash
./gradlew build
```

### Run IDE with Plugin

```bash
./gradlew runIde
```

### Test

```bash
./gradlew test
```

---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).
