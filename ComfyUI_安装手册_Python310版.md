# ComfyUI 安装手册（Python 3.10 锁版本版）

更新时间：2026-05-29  
适用系统：Windows 10/11  
推荐 ComfyUI 版本：`v0.22.0`  
推荐 Python 版本：`Python 3.10.11`  
推荐安装方式：手动安装 + 独立虚拟环境 `.venv`

> 本手册专门面向需要使用 Python 3.10 的 ComfyUI 环境。不要使用官方 portable 包里的 Python 3.13，也不要把依赖装进系统 Python。

## 1. 推荐版本组合

| 组件 | 推荐版本 |
| --- | --- |
| ComfyUI | `v0.22.0` |
| Python | `3.10.11` |
| PyTorch | 优先使用支持 Python 3.10 的稳定 CUDA 版 |
| CUDA 选择 | RTX 20/30/40/50 优先 `cu128`，老显卡优先 `cu126` |
| 安装方式 | `venv` 虚拟环境 |

为什么推荐 Python `3.10.11`：

- 兼容大量 ComfyUI 自定义节点；
- 比 Python 3.13 更适合老插件；
- 比系统混装更容易维护；
- 后续出现依赖冲突时，删除 `.venv` 重建即可。

## 2. 不推荐的安装方式

本手册不推荐：

- 使用系统 Python 直接安装；
- 多个 AI 项目共用同一个 Python 环境；
- 直接追 ComfyUI `master` 分支；
- 在中文路径、桌面、OneDrive 下安装；
- 混用 portable 包里的 `python_embeded` 和系统 Python。

## 3. 安装前准备

### 3.1 安装 NVIDIA 驱动

如果你使用 NVIDIA 显卡，先安装最新版 Game Ready Driver 或 Studio Driver。

安装后打开 PowerShell：

```powershell
nvidia-smi
```

确认能看到：

- 显卡型号；
- Driver Version；
- CUDA Version。

如果 `nvidia-smi` 不存在，先处理显卡驱动，不要继续安装 ComfyUI。

### 3.2 安装 Git

下载 Git for Windows：

```text
https://git-scm.com/download/win
```

安装后验证：

```powershell
git --version
```

### 3.3 安装 Python 3.10.11

下载 Python `3.10.11`：

```text
https://www.python.org/downloads/release/python-31011/
```

安装时注意：

- 勾选 `Add python.exe to PATH`；
- 选择 `Customize installation`；
- 确保安装 `pip`；
- 建议安装到默认用户目录，或自定义英文路径。

安装后验证：

```powershell
py -3.10 --version
```

应输出：

```text
Python 3.10.11
```

如果显示的不是 3.10，请检查 Python Launcher 或 PATH。

## 4. 创建安装目录

建议使用英文短路径：

```powershell
mkdir D:\AI
cd D:\AI
```

不要使用类似路径：

```text
C:\Users\你的名字\Desktop
D:\我的AI工具\ComfyUI
OneDrive\ComfyUI
```

## 5. 下载并锁定 ComfyUI 版本

```powershell
cd D:\AI
git clone https://github.com/Comfy-Org/ComfyUI.git
cd ComfyUI
git fetch --tags
git checkout v0.22.0
```

确认当前版本：

```powershell
git describe --tags
```

应看到：

```text
v0.22.0
```

锁定 release tag 的目的：避免以后 ComfyUI 更新导致自定义节点突然失效。

## 6. 创建 Python 3.10 虚拟环境

确认在 ComfyUI 根目录：

```powershell
cd D:\AI\ComfyUI
py -3.10 -m venv .venv
.\.venv\Scripts\activate
python --version
```

应输出：

```text
Python 3.10.11
```

升级 pip：

```powershell
python -m pip install --upgrade pip setuptools wheel
```

以后每次启动或安装依赖前，都要先激活虚拟环境：

```powershell
cd D:\AI\ComfyUI
.\.venv\Scripts\activate
```

## 7. 安装 PyTorch

PyTorch 是最容易版本冲突的地方。正确顺序是：

1. 先激活 `.venv`；
2. 先安装 PyTorch；
3. 再安装 `requirements.txt`；
4. 最后安装自定义节点。

### 7.1 推荐方案：CUDA 12.8

适合大多数 RTX 20/30/40/50 系列 NVIDIA 显卡：

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

### 7.2 老显卡方案：CUDA 12.6

如果是 GTX 10 系列、较老 Quadro，或遇到 CUDA kernel 报错，使用：

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### 7.3 CPU 测试方案

没有 NVIDIA 显卡时可以先装 CPU 版：

```powershell
pip install torch torchvision torchaudio
```

CPU 版只能测试功能，出图会很慢。

### 7.4 验证 PyTorch

```powershell
python -c "import torch; print('torch:', torch.__version__); print('cuda:', torch.version.cuda); print('available:', torch.cuda.is_available()); print('gpu:', torch.cuda.get_device_name(0) if torch.cuda.is_available() else 'CPU')"
```

NVIDIA CUDA 正常时应显示：

```text
available: True
```

如果显示 `False`，先不要安装 ComfyUI 依赖，优先检查 PyTorch 是否装成 CPU 版。

## 8. 安装 ComfyUI 依赖

确认虚拟环境已激活：

```powershell
cd D:\AI\ComfyUI
.\.venv\Scripts\activate
pip install -r requirements.txt
```

如果安装过程提示某些包要求更高 Python 版本，不要升级 Python，先记录报错包名，再判断是否需要固定旧版本。

## 9. 启动 ComfyUI

```powershell
cd D:\AI\ComfyUI
.\.venv\Scripts\activate
python main.py
```

启动成功后打开：

```text
http://127.0.0.1:8188
```

低显存启动：

```powershell
python main.py --lowvram
```

局域网访问：

```powershell
python main.py --listen 0.0.0.0 --port 8188
```

局域网访问只建议在可信网络中使用。

## 10. 安装 ComfyUI Manager

进入 `custom_nodes`：

```powershell
cd D:\AI\ComfyUI\custom_nodes
git clone https://github.com/Comfy-Org/ComfyUI-Manager.git
cd D:\AI\ComfyUI
python main.py
```

启动后界面中会出现 Manager。

建议：

- 不要一次安装太多自定义节点；
- 每装 1 到 3 个节点就重启测试；
- 如果启动失败，先把新装节点移出 `custom_nodes`。

## 11. 模型目录

常用目录：

```text
D:\AI\ComfyUI\models\checkpoints
D:\AI\ComfyUI\models\vae
D:\AI\ComfyUI\models\loras
D:\AI\ComfyUI\models\controlnet
D:\AI\ComfyUI\models\clip
D:\AI\ComfyUI\models\clip_vision
D:\AI\ComfyUI\models\unet
D:\AI\ComfyUI\models\diffusion_models
D:\AI\ComfyUI\models\text_encoders
```

常见模型放置：

| 模型类型 | 目录 |
| --- | --- |
| SD 1.5 / SDXL checkpoint | `models\checkpoints` |
| VAE | `models\vae` |
| LoRA | `models\loras` |
| ControlNet | `models\controlnet` |
| FLUX diffusion model | `models\diffusion_models` 或 `models\unet` |
| CLIP / T5 | `models\text_encoders` 或 `models\clip` |

不同模型作者可能要求不同目录，优先看模型页面说明。

## 12. 推荐启动脚本

在 `D:\AI\ComfyUI` 新建 `start_comfyui_py310.bat`：

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

## 13. 防止版本冲突

### 13.1 确认正在使用 Python 3.10

```powershell
where python
python --version
```

正确路径应类似：

```text
D:\AI\ComfyUI\.venv\Scripts\python.exe
```

正确版本应是：

```text
Python 3.10.11
```

### 13.2 确认 torch 没被替换

```powershell
pip show torch torchvision torchaudio
```

如果安装自定义节点后 torch 被改坏，可以重装：

```powershell
pip uninstall torch torchvision torchaudio -y
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

老显卡改用：

```powershell
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### 13.3 不要混用多个 ComfyUI

如果你有多个 ComfyUI：

```text
D:\AI\ComfyUI_SDXL
D:\AI\ComfyUI_FLUX
D:\AI\ComfyUI_VIDEO
```

每个目录都应该有自己的 `.venv`。

## 14. 常见报错

### 14.1 `Torch not compiled with CUDA enabled`

原因：装成 CPU 版 torch。

处理：

```powershell
pip uninstall torch torchvision torchaudio -y
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu128
```

### 14.2 `CUDA out of memory`

原因：显存不足。

处理：

- 降低分辨率；
- 减少 batch size；
- 使用 `--lowvram`；
- 使用 fp8 / quant 模型；
- 关闭占显存的软件。

### 14.3 `no kernel image is available for execution on the device`

原因：当前 CUDA 版 PyTorch 不支持你的老显卡。

处理：

```powershell
pip uninstall torch torchvision torchaudio -y
pip install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu126
```

### 14.4 自定义节点缺依赖

进入节点目录：

```powershell
cd D:\AI\ComfyUI\custom_nodes\节点目录名
pip install -r requirements.txt
```

如果节点不支持 Python 3.10，一般会在安装时报 `Requires-Python` 或编译错误。此时优先换节点版本，不要直接升级整个 Python。

### 14.5 页面有红色节点

常见原因：

- 模型没放对目录；
- 节点依赖没安装；
- 节点版本和 ComfyUI 不匹配；
- Python 3.10 环境下某个新节点要求 Python 3.11+；
- torch 被其他依赖覆盖。

处理顺序：

1. 看启动终端的第一条报错；
2. 查具体节点目录；
3. 安装该节点 requirements；
4. 仍失败时回退节点版本；
5. 最后再考虑新建 Python 3.11/3.12 环境。

## 15. 更新策略

稳定使用时不要天天更新。

建议：

- 项目进行中不更新；
- 更新前备份 `user`、`custom_nodes`、`models`；
- 新模型或新节点先复制一套环境测试；
- 升级 ComfyUI 时只切换 release tag。

备份目录：

```text
D:\AI\ComfyUI\user
D:\AI\ComfyUI\custom_nodes
D:\AI\ComfyUI\models
```

保持当前版本：

```powershell
git checkout v0.22.0
```

以后升级到新 release：

```powershell
git fetch --tags
git checkout 新版本tag
pip install -r requirements.txt
```

## 16. 最小可用检查清单

安装完成后检查：

- `python --version` 是 `Python 3.10.11`；
- `where python` 指向 `D:\AI\ComfyUI\.venv\Scripts\python.exe`；
- `pip show torch` 显示的是 CUDA 版；
- `torch.cuda.is_available()` 是 `True`；
- ComfyUI 能打开 `http://127.0.0.1:8188`；
- 第一次测试用 `512x512` 小图；
- 安装自定义节点后重启没有红色报错。

## 17. 推荐最终配置

```text
ComfyUI: v0.22.0
Python: 3.10.11
环境: D:\AI\ComfyUI\.venv
PyTorch: CUDA 12.8 版，老显卡改 CUDA 12.6
启动: python main.py --listen 127.0.0.1 --port 8188
更新: 只按 release tag 升级，不追 master
```

## 18. 参考来源

- ComfyUI GitHub Releases：`https://github.com/Comfy-Org/ComfyUI/releases`
- ComfyUI 官方系统要求：`https://docs.comfy.org/installation/system_requirements/`
- ComfyUI 官方手动安装文档：`https://docs.comfy.org/installation/manual_install`
- PyTorch 官方安装页：`https://pytorch.org/get-started/locally/`
- Python 3.10.11 下载页：`https://www.python.org/downloads/release/python-31011/`
