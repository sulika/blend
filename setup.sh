#!/usr/bin/env bash
#
# Interactive setup wizard for Blend's Docker workflow.
# Manages application.yml (your Markdown sources) and the matching
# docker-compose.override.yml bind mounts, then starts the app.
#
set -uo pipefail
cd "$(dirname "${BASH_SOURCE[0]}")"

CONFIG_FILE="application.yml"
OVERRIDE_FILE="docker-compose.override.yml"

if [ -t 1 ] && command -v tput >/dev/null 2>&1; then
  BOLD=$(tput bold); RESET=$(tput sgr0)
  GREEN=$(tput setaf 2); YELLOW=$(tput setaf 3); RED=$(tput setaf 1); CYAN=$(tput setaf 6)
else
  BOLD=""; RESET=""; GREEN=""; YELLOW=""; RED=""; CYAN=""
fi

info()    { echo "${CYAN}==>${RESET} $*"; }
success() { echo "${GREEN}OK${RESET} $*"; }
warn()    { echo "${YELLOW}!${RESET} $*"; }
fail()    { echo "${RED}FAIL${RESET} $*" >&2; }

prompt() {
  # prompt "Question" ["default"]
  local q="$1" d="${2:-}" ans
  if [ -n "$d" ]; then
    read -r -p "${q} [${d}]: " ans
    printf '%s' "${ans:-$d}"
  else
    read -r -p "${q}: " ans
    printf '%s' "$ans"
  fi
}

require_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    fail "Docker is not installed. Install Docker Desktop (or the Docker Engine) first."
    return 1
  fi
  if ! docker compose version >/dev/null 2>&1; then
    fail "Docker Compose v2 not found (the 'docker compose' subcommand). Update Docker."
    return 1
  fi
}

# ---- application.yml management ------------------------------------------

write_header() {
  # write_header "<title>" "<theme>"
  cat > "$CONFIG_FILE" <<EOF
app:
  markdown-viewer:
    enabled: true
    html:
      title: "${1}"
      theme: ${2}
      css:
        - ./themes/${2}.css
    sources:
EOF
}

existing_ids() {
  [ -f "$CONFIG_FILE" ] || return 0
  grep -E '^[[:space:]]*- id:' "$CONFIG_FILE" | sed -E 's/^[[:space:]]*- id:[[:space:]]*//'
}

add_source() {
  local id name root indexFile ans

  while true; do
    id=$(prompt "Source id (slug, e.g. english-teaching)")
    if [[ ! "$id" =~ ^[a-z0-9-]+$ ]]; then
      warn "id must be lowercase letters, numbers, and hyphens only"
      continue
    fi
    if existing_ids | grep -qx "$id"; then
      warn "id '$id' already exists"
      continue
    fi
    break
  done

  name=$(prompt "Display name" "$id")

  while true; do
    root=$(prompt "Absolute path to the Markdown folder on this machine")
    if [ ! -d "$root" ]; then
      warn "'$root' doesn't look like a directory on this machine"
      ans=$(prompt "Use it anyway? (y/N)" "N")
      case "$ans" in [yY]*) break ;; *) continue ;; esac
    fi
    break
  done

  indexFile=$(prompt "Index file (opened by default)" "README.md")

  cat >> "$CONFIG_FILE" <<EOF
      - id: ${id}
        name: ${name}
        root: ${root}
        indexFile: ${indexFile}
EOF
  success "Added source '${id}'"
}

remove_source() {
  if [ ! -f "$CONFIG_FILE" ] || [ -z "$(existing_ids)" ]; then
    warn "No sources configured yet."
    return
  fi
  list_sources
  local target
  target=$(prompt "id to remove (blank to cancel)")
  [ -z "$target" ] && return
  if ! existing_ids | grep -qx "$target"; then
    warn "No source with id '$target'"
    return
  fi
  awk -v target="$target" '
    /^[[:space:]]*- id:/ {
      line=$0; sub(/^[[:space:]]*- id:[[:space:]]*/,"",line)
      skip = (line == target) ? 1 : 0
    }
    skip != 1 { print }
  ' "$CONFIG_FILE" > "${CONFIG_FILE}.tmp" && mv "${CONFIG_FILE}.tmp" "$CONFIG_FILE"
  success "Removed '${target}'"
}

list_sources() {
  if [ ! -f "$CONFIG_FILE" ] || [ -z "$(existing_ids)" ]; then
    warn "No sources configured yet."
    return
  fi
  awk '
    /^[[:space:]]*- id:/ {
      if (have) printf("  - %s (%s)\n      %s\n", id, name, root)
      sub(/^[[:space:]]*- id:[[:space:]]*/,""); id=$0; have=1; next
    }
    /^[[:space:]]*name:/ { sub(/^[[:space:]]*name:[[:space:]]*/,""); name=$0; next }
    /^[[:space:]]*root:/ { sub(/^[[:space:]]*root:[[:space:]]*/,""); root=$0; next }
    END { if (have) printf("  - %s (%s)\n      %s\n", id, name, root) }
  ' "$CONFIG_FILE"
}

init_wizard() {
  info "No ${CONFIG_FILE} found — let's set one up."
  local title theme
  title=$(prompt "Site title" "My Notes")
  theme=$(prompt "Theme" "github")
  write_header "$title" "$theme"

  while true; do
    add_source
    local more
    more=$(prompt "Add another source? (y/N)" "N")
    case "$more" in [yY]*) continue ;; *) break ;; esac
  done
}

# ---- docker-compose.override.yml management -------------------------------

sync_override() {
  if [ ! -f "$CONFIG_FILE" ]; then
    fail "${CONFIG_FILE} not found — run the wizard first."
    return 1
  fi
  local roots count=0
  roots=$(grep -E '^[[:space:]]*root:' "$CONFIG_FILE" | sed -E 's/^[[:space:]]*root:[[:space:]]*//')

  {
    echo "services:"
    echo "  blend:"
    echo "    volumes:"
    while IFS= read -r r; do
      [ -z "$r" ] && continue
      printf '      - %s:%s:ro\n' "$r" "$r"
      count=$((count + 1))
    done <<< "$roots"
  } > "$OVERRIDE_FILE"

  success "Wrote ${OVERRIDE_FILE} with ${count} mount(s)"
}

start_app() {
  sync_override || return 1
  require_docker || return 1
  info "Starting Blend (docker compose up --build)… Ctrl+C to stop."
  docker compose up --build
}

# ---- main menu --------------------------------------------------------------

main_menu() {
  while true; do
    echo
    echo "${BOLD}Blend setup${RESET}"
    echo "  1) Add a source"
    echo "  2) Remove a source"
    echo "  3) List sources"
    echo "  4) Sync Docker mounts only"
    echo "  5) Start the app (sync + docker compose up)"
    echo "  0) Exit"
    local choice
    choice=$(prompt "Choose an option" "5")
    case "$choice" in
      1) add_source ;;
      2) remove_source ;;
      3) list_sources ;;
      4) sync_override ;;
      5) start_app; break ;;
      0) exit 0 ;;
      *) warn "Invalid option" ;;
    esac
  done
}

main() {
  if [ ! -f "$CONFIG_FILE" ]; then
    init_wizard
  fi
  main_menu
}

main "$@"
