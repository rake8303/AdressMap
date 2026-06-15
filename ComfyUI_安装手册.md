# ComfyUI 安装手册（锁版本版）

更新时间：2026-05-29  
适用对象：Windows 10/11 为主，兼顾 Linux/macOS  
推荐稳定版本：ComfyUI `v0.22.0`（2026-05-20 最新稳定版）

> 本手册的核心原则：不要把 ComfyUI、Python、PyTorch、CUDA 混装在系统环境里。一定使用独立目录或虚拟环境，避免版本冲突。

## 1. 推荐版本组合

### 1.1 Windows 新手推荐：Portable 版

| 组件 | 推荐版本 |
| --- | --- |
| ComfyUI | `v0.22.0` 对应 release/portable 包 |
| Python | Portable 包内置版本，当前官方 portable 为 Python `3.13` |
| PyTorch | Portable 包内置版本，当前官方 portable 为 CUDA `13.0` 方向 |
| NVIDIA 驱动 | 建议使用最新版 Game Ready / Studio Driver |

适合人群：不想折腾 Python、pip、CUDA 的用户。

优点：

- 解压即可用；
- 不污染系统 Python；
- PyTorch/CUDA 已经由官方打包。

注意：

- 老显卡可能不适合 CUDA 13.0，例如 GTX 10 系列、部分老 Quadro。老显卡建议走手动安装，并选择 CUDA 12.6/12.8 的 PyTorch。
- 不要把 portable 包里的 `python_embeded` 当作系统 Python 使用。

### 1.2 Windows 进阶推荐：手动安装锁版本

| 组件 | 推荐版本 |
| --- | --- |
| ComfyUI | `v0.22.0` |
| Python | `3.12.x`（推荐）或 `3.13.x` |
| PyTorch | `2.7.0` 或更新稳定版 |
| CUDA 选择 | 新 RTX 显卡优先 CUDA 12.8/13.0；老显卡优先 CUDA 12.6 |

推荐 Python `3.12.x` 的原因：它对很多自定义节点更稳。Python `3.13` 能跑官方 portable，但部分第三方节点可能还没完全适配。

### 1.3 Linux/macOS 简表

| 平台 | 建议 |
| --- | --- |
| Linux + NVIDIA | Python `3.12.x` + venv/conda + 对应 CUDA 的 PyTorch |
| Linux + AMD | 使用官方 ROCm 版本，按显卡和 ROCm 兼容表选择 |
| macOS Apple Silicon | Python `3.12.x`，使用 PyTorch MPS，加速能力弱于 NVIDIA CUDA |

## 2. 安装前准备

### 2.1 检查显卡

Windows 打开 PowerShell：

```powershell
nvidia-smi
```

看三项：

- `Driver Version`：NVIDIA 驱动版本；
- `CUDA Version`：驱动支持的最高 CUDA 运行版本；
- GPU 型号：例如 RTX 3060、RTX 4090、RTX 5090、GTX 1080 Ti。

如果提示找不到 `nvidia-smi`：

- 没装 NVIDIA 驱动；
- 或显卡不是 NVIDIA；
- 或环境变量未配置，但多数情况下先更新驱动即可。

### 2.2 安装基础工具

手动安装需要：

- Git：用于下载 ComfyUI 和自定义节点；
- Python：推荐 `3.12.x`；
- 7-Zip：用于解压模型或 portable 包；
- Visual Studio Build Tools：部分自定义节点编译依赖可能需要。

## 3. 方案 A：Windows Portable 安装

这是最推荐给普通用户的方式。

### 3.1 下载

进入 ComfyUI GitHub Releases：

```text
https://github.com/Comfy-Org/ComfyUI/releases
```

选择 `v0.22.0` 的 Windows portable 压缩包。

### 3.2 解压目录建议

建议解压到英文路径，例如：

```text
D:\AI\ComfyUI_windows_portable
```

不要放在：

- 桌面；
- OneDrive；
- 中文路径；
- 路径很深的目录；
- `Program Files`。

### 3.3 启动

进入解压目录，双击：

```text
run_nvidia_gpu.bat
```

启动成功后，浏览器打开：

```text
http://127.0.0.1:8188
```

如果显存较小，可以尝试：

```text
run_nvidia_gpu_lowvram.bat
```

## 4. 方案 B：手动安装（推荐高手使用）

以下以 Windows + PowerShell 为例。

### 4.1 创建目录

```powershell
mkdir D:\AI
cd D:\AI
```

### 4.2 克隆指定版本

```powershell
git clone https://github.com/Comfy-Org/ComfyUI.git
cd ComfyUI
git checkout v0.22.0
```

锁定 `v0.22.0` 的目的：避免直接追 master 导致自定义节点突然失效。

### 4.3 创建虚拟环境

如果使用 Python `3.12`：

```powershell
py -3.12 -m venv .venv
.\.venv\Scripts\activate
python -m pip install --upgrade pip
```

验证：

```powershell
python --version
pip --version
```

应看到 Python `3.12.x`。

## 5. 安装 PyTorch

PyTorch 是最容易冲突的地方。不要先装一堆 requirements，再补 torch。正确顺序是：

1. 先激活虚拟环境；
2. 先安装适合显卡的 PyTorch；
3. 再安装 ComfyUI requirements。

### 5.1 新 NVIDIA 显卡：CUDA 12.8

适合 RTX 20/30/40/50 系列大多数用户：

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

### 5.2 最新 NVIDIA 显卡：CUDA 13.0

如果你的驱动足够新，并且显卡较新，可用：

```powershell
pip install torch torchvision torchaudio --extra-index-url https://download.pytorch.org/whl/cu130
```

### 5.3 老 NVIDIA 显卡：CUDA 12.6

适合部分老卡，尤其是遇到 `no kernel image is available for execution on the device` 时：

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### 5.4 CPU 版

没有独显或只想测试界面：

```powershell
pip install torch torchvision torchaudio
```

CPU 版能启动，但出图会非常慢。

### 5.5 验证 PyTorch

```powershell
python -c "import torch; print('torch:', torch.__version__); print('cuda:', torch.version.cuda); print('cuda_available:', torch.cuda.is_available()); print('gpu:', torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'CPU')"
```

正常 NVIDIA CUDA 环境应显示：

```text
cuda_available: True
```

如果是 `False`，不要继续安装一堆节点，先解决 torch/CUDA。

## 6. 安装 ComfyUI 依赖

确认仍在 ComfyUI 目录，且虚拟环境已激活：

```powershell
pip install -r requirements.txt
```

启动：

```powershell
python main.py
```

浏览器打开：

```text
http://127.0.0.1:8188
```

## 7. 模型目录放置

ComfyUI 常用模型目录：

```text
ComfyUI\models\checkpoints
ComfyUI\models\vae
ComfyUI\models\loras
ComfyUI\models\controlnet
ComfyUI\models\clip
ComfyUI\models\clip_vision
ComfyUI\models\unet
ComfyUI\models\diffusion_models
ComfyUI\models\text_encoders
```

常见放法：

| 模型类型 | 放置目录 |
| --- | --- |
| SD 1.5 / SDXL checkpoint | `models\checkpoints` |
| VAE | `models\vae` |
| LoRA | `models\loras` |
| ControlNet | `models\controlnet` |
| FLUX diffusion model | `models\diffusion_models` 或 `models\unet`，按模型说明为准 |
| CLIP / T5 文本编码器 | `models\text_encoders` 或 `models\clip` |

不同模型作者的目录要求可能不同，优先看模型页面说明。

## 8. 安装 ComfyUI Manager

推荐安装 ComfyUI Manager 管理自定义节点。

进入 ComfyUI 目录：

```powershell
cd D:\AI\ComfyUI\custom_nodes
git clone https://github.com/Comfy-Org/ComfyUI-Manager.git
cd ..
python main.py
```

启动后界面里会出现 Manager。

注意：

- 不要一次性安装几十个自定义节点；
- 每装 1 到 3 个节点就重启测试；
- 节点报错时先看它要求的 Python / torch / xformers / CUDA 版本。

## 9. 防止版本冲突的规则

### 9.1 固定 ComfyUI 版本

当前推荐固定：

```powershell
git checkout v0.22.0
```

以后要升级前，先备份：

```text
ComfyUI\user
ComfyUI\custom_nodes
ComfyUI\models
```

### 9.2 不要混用 Python

启动前检查：

```powershell
where python
python --version
```

如果你用虚拟环境，第一条路径应指向：

```text
D:\AI\ComfyUI\.venv\Scripts\python.exe
```

### 9.3 不要让 pip 随便升级 torch

安装某个插件依赖时，如果它试图重装 torch，要谨慎。可以先查看当前 torch：

```powershell
pip show torch torchvision torchaudio
```

必要时重新安装指定 CUDA 版本：

```powershell
pip install --force-reinstall torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

### 9.4 每个 ComfyUI 单独环境

如果你同时玩 SDXL、FLUX、Wan、Hunyuan、各种视频节点，建议多个目录：

```text
D:\AI\ComfyUI_SDXL
D:\AI\ComfyUI_FLUX
D:\AI\ComfyUI_VIDEO
```

每个目录独立 `.venv`，不要共用环境。

## 10. 常见报错处理

### 10.1 `Torch not compiled with CUDA enabled`

原因：装成 CPU 版 torch。

处理：

```powershell
pip uninstall torch torchvision torchaudio -y
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

### 10.2 `CUDA out of memory`

原因：显存不足。

处理：

- 降低分辨率；
- 减少 batch size；
- 使用 `--lowvram` 或 `--novram`；
- 使用 fp8 / quant 版本模型；
- 关闭占显存的软件。

启动示例：

```powershell
python main.py --lowvram
```

### 10.3 `no kernel image is available for execution on the device`

原因：PyTorch CUDA 版本不支持你的老显卡。

处理：改装 CUDA 12.6 版 PyTorch：

```powershell
pip uninstall torch torchvision torchaudio -y
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### 10.4 自定义节点缺包

进入对应节点目录：

```powershell
cd D:\AI\ComfyUI\custom_nodes\节点目录名
pip install -r requirements.txt
```

如果安装后 ComfyUI 起不来，先把该节点目录移出 `custom_nodes`，确认是不是它导致。

### 10.5 页面能打开但节点红色

常见原因：

- 模型文件没放对；
- 自定义节点依赖没装；
- 自定义节点版本太旧；
- ComfyUI 版本太新或太旧；
- Python 3.13 下某些节点不兼容。

处理顺序：

1. 看终端报错；
2. 更新该节点；
3. 安装该节点 requirements；
4. 仍失败时换 Python `3.12.x` 环境。

## 11. 更新建议

稳定工作机不要天天更新。推荐节奏：

- 项目正在做：不更新；
- 项目结束后：备份再更新；
- 需要新模型节点：单独复制一份环境测试；
- 大版本升级：先看 release notes 和节点 issue。

更新 ComfyUI：

```powershell
git fetch --tags
git checkout v0.22.0
pip install -r requirements.txt
```

如果以后要升级到新版本，把 `v0.22.0` 换成目标 release tag。

## 12. 推荐启动脚本

在 ComfyUI 根目录新建 `start_comfyui.bat`：

```bat
@echo off
cd /d D:\AI\ComfyUI
call .venv\Scripts\activate
python main.py --listen 127.0.0.1 --port 8188
pause
```

低显存版本：

```bat
@echo off
cd /d D:\AI\ComfyUI
call .venv\Scripts\activate
python main.py --listen 127.0.0.1 --port 8188 --lowvram
pause
```

局域网访问版本：

```bat
@echo off
cd /d D:\AI\ComfyUI
call .venv\Scripts\activate
python main.py --listen 0.0.0.0 --port 8188
pause
```

局域网访问有安全风险，只建议在可信内网使用。

## 13. 最小可用检查清单

安装完成后逐项检查：

- `python --version` 是预期版本；
- `pip show torch` 显示的是 CUDA 版 torch；
- `torch.cuda.is_available()` 是 `True`；
- ComfyUI 能打开 `http://127.0.0.1:8188`；
- checkpoint / VAE / LoRA 放在正确目录；
- 首次生成用小分辨率测试，例如 `512x512`；
- 安装自定义节点后重启无红字报错。

## 14. 建议的稳定配置

如果你要一套长期稳定的生产环境，建议：

```text
ComfyUI: v0.22.0
Python: 3.12.x
PyTorch: 2.7.0+cu128 或官方当前稳定 CUDA 包
安装方式: venv 手动安装
更新策略: 不自动追 master，只按 release tag 升级
```

如果你是普通 Windows 用户，建议：

```text
安装方式: 官方 Windows portable
ComfyUI: v0.22.0 release
Python/PyTorch: 使用 portable 内置版本
更新策略: 新版本先解压成新目录测试，不覆盖旧目录
```

## 15. 参考来源

- ComfyUI GitHub Releases：`https://github.com/Comfy-Org/ComfyUI/releases`
- ComfyUI 官方系统要求：`https://docs.comfy.org/installation/system_requirements/`
- ComfyUI 官方手动安装文档：`https://docs.comfy.org/installation/manual_install`
- PyTorch 官方安装页：`https://pytorch.org/get-started/locally/`
