#!/usr/bin/env bash
# Installs the pinned development tools inside the project and enables the git hooks, on macOS.
set -euo pipefail

readonly GITLEAKS_VERSION="8.30.1"
readonly GITLEAKS_ARM64_SHA256="b40ab0ae55c505963e365f271a8d3846efbc170aa17f2607f13df610a9aeb6a5"
readonly GITLEAKS_X64_SHA256="dfe101a4db2255fc85120ac7f3d25e4342c3c20cf749f2c20a18081af1952709"
readonly GITLEAKS_RELEASES="https://github.com/gitleaks/gitleaks/releases/download"

root="$(git rev-parse --show-toplevel)"
readonly root
readonly tools_dir="${root}/.tools"
readonly gitleaks_bin="${tools_dir}/gitleaks"

# The Apple chips are arm64 and the Intel Macs x86_64; each has its own gitleaks build.
gitleaks_arch() {
  case "$(uname -sm)" in
    "Darwin arm64") echo "arm64" ;;
    "Darwin x86_64") echo "x64" ;;
    *)
      echo "setup-dev-macos: only macOS is supported; got $(uname -sm)" >&2
      exit 1
      ;;
  esac
}

install_gitleaks() {
  local arch asset sha
  arch="$(gitleaks_arch)"
  asset="gitleaks_${GITLEAKS_VERSION}_darwin_${arch}.tar.gz"
  sha="${GITLEAKS_ARM64_SHA256}"
  [[ "${arch}" == "x64" ]] && sha="${GITLEAKS_X64_SHA256}"

  local download_dir
  download_dir="$(mktemp -d)"
  trap 'rm -rf "${download_dir}"' RETURN

  curl -fsSL "${GITLEAKS_RELEASES}/v${GITLEAKS_VERSION}/${asset}" -o "${download_dir}/${asset}"
  echo "${sha}  ${download_dir}/${asset}" | shasum -a 256 --check --quiet

  mkdir -p "${tools_dir}"
  tar -xzf "${download_dir}/${asset}" -C "${tools_dir}" gitleaks
  chmod +x "${gitleaks_bin}"
}

if [[ ! -x "${gitleaks_bin}" ]] || [[ "$("${gitleaks_bin}" version)" != "${GITLEAKS_VERSION}" ]]; then
  install_gitleaks
fi

git -C "${root}" config core.hooksPath scripts/development/git-hooks

echo "setup-dev-macos: gitleaks $("${gitleaks_bin}" version) installed and git hooks enabled"
