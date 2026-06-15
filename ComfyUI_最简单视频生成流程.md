# ComfyUI 最简单视频生成流程（图片转视频）

更新时间：2026-05-29  
适用环境：已安装好的 ComfyUI，推荐 Python 3.10 环境  
推荐方式：图片转视频（Image to Video）  
推荐模型：Stable Video Diffusion / SVD  
目标：用一张图片生成一个 2 到 4 秒的简单视频

> 最简单的视频生成方式不是“文字直接生成视频”，而是“先有一张图，再让图动起来”。这样节点少、报错少、显存压力也更低。

## 1. 需要准备什么

只需要 4 样东西：

| 项目 | 说明 |
| --- | --- |
| ComfyUI | 已经能正常打开 `http://127.0.0.1:8188` |
| 一张图片 | 建议 `1024x576` 或 `576x1024` |
| SVD 视频模型 | 用来把图片变成视频 |
| Video Helper Suite | 用来保存 mp4 视频 |

## 2. 安装 Video Helper Suite

如果已经安装了 ComfyUI Manager：

1. 打开 ComfyUI；
2. 点击 `Manager`；
3. 点击 `Custom Nodes Manager`；
4. 搜索：

```text
ComfyUI-VideoHelperSuite
```

5. 点击安装；
6. 安装完成后重启 ComfyUI。

如果不用 Manager，也可以手动安装：

```powershell
cd D:\AI\ComfyUI\custom_nodes
git clone https://github.com/Kosinkadink/ComfyUI-VideoHelperSuite.git
cd D:\AI\ComfyUI
.\.venv\Scripts\activate
pip install -r custom_nodes\ComfyUI-VideoHelperSuite\requirements.txt
python main.py
```

## 3. 下载 SVD 模型

推荐先使用 SVD-XT，视频帧数更多。

下载地址：

```text
https://huggingface.co/stabilityai/stable-video-diffusion-img2vid-xt
```

下载模型文件后，放到：

```text
D:\AI\ComfyUI\models\checkpoints
```

常见文件名类似：

```text
svd_xt.safetensors
```

如果显存较小，也可以使用普通 SVD：

```text
https://huggingface.co/stabilityai/stable-video-diffusion-img2vid
```

## 4. 准备一张图片

建议图片尺寸：

```text
1024x576 横屏
576x1024 竖屏
```

最简单的测试图可以是：

- 一个人物半身照；
- 一辆车；
- 一只产品图；
- 一个风景图。

图片不要太小，也不要太模糊。

## 5. 最简单节点流程

在 ComfyUI 里，新建一个工作流，使用以下节点：

```text
Load Image
Image Only Checkpoint Loader
SVD_img2vid_Conditioning
VideoLinearCFGGuidance
KSampler
VAEDecode
VHS_VideoCombine
```

节点连接顺序：

```text
Load Image
  -> SVD_img2vid_Conditioning

Image Only Checkpoint Loader
  -> MODEL -> VideoLinearCFGGuidance -> KSampler
  -> CLIP_VISION -> SVD_img2vid_Conditioning
  -> VAE -> VAEDecode

SVD_img2vid_Conditioning
  -> positive -> KSampler positive
  -> negative -> KSampler negative
  -> latent -> KSampler latent_image

KSampler
  -> samples -> VAEDecode

VAEDecode
  -> IMAGE -> VHS_VideoCombine
```

如果你不想手动连节点，可以在 ComfyUI 里找模板：

```text
Browse Templates -> Video -> Stable Video Diffusion / Image to Video
```

不同版本模板名字可能略有不同，看到 `SVD`、`img2vid`、`Image to Video` 都可以尝试。

## 6. 推荐参数

第一次测试用保守参数：

### SVD_img2vid_Conditioning

| 参数 | 推荐值 |
| --- | --- |
| width | `1024` |
| height | `576` |
| video_frames | `14` 或 `25` |
| motion_bucket_id | `127` |
| fps | `6` |
| augmentation_level | `0.00` |

说明：

- `video_frames=14`：更快，更省显存；
- `video_frames=25`：更顺，但更慢；
- `motion_bucket_id` 越高，动作越大；
- `augmentation_level` 越高，越容易偏离原图。

### KSampler

| 参数 | 推荐值 |
| --- | --- |
| seed | 随机或固定数字 |
| steps | `20` |
| cfg | `2.5` |
| sampler_name | `euler` |
| scheduler | `normal` |
| denoise | `1.00` |

### VideoLinearCFGGuidance

| 参数 | 推荐值 |
| --- | --- |
| min_cfg | `1.0` |

### VHS_VideoCombine

| 参数 | 推荐值 |
| --- | --- |
| frame_rate | `6` |
| format | `video/h264-mp4` 或 `video/webm` |
| filename_prefix | `svd_test` |

如果没有 mp4 选项，先选 `webm`。WebM 更容易成功。

## 7. 点击生成

确认：

1. `Load Image` 已经选择图片；
2. `Image Only Checkpoint Loader` 选择了 `svd_xt.safetensors`；
3. `VHS_VideoCombine` 格式选了 mp4 或 webm；
4. 点击 `Queue Prompt`。

生成完成后，视频通常在：

```text
D:\AI\ComfyUI\output
```

文件名类似：

```text
svd_test_00001.mp4
svd_test_00001.webm
```

## 8. 最省事参数模板

第一次只改这几个：

```text
图片：选择一张 1024x576 图片
模型：svd_xt.safetensors
video_frames：14
fps：6
motion_bucket_id：127
steps：20
cfg：2.5
输出：webm 或 mp4
```

生成结果大约是：

```text
14 帧 / 6 fps = 约 2.3 秒视频
25 帧 / 6 fps = 约 4.1 秒视频
```

## 9. 显存不够怎么办

如果报 `CUDA out of memory`：

1. 把 `video_frames` 改成 `14`；
2. 不要用太大的图片；
3. 启动 ComfyUI 时加：

```powershell
python main.py --lowvram
```

4. 关闭其他占显存的软件；
5. 如果还不行，先用 WebM 输出，不用 mp4。

## 10. 常见问题

### 10.1 找不到 VHS_VideoCombine

说明 Video Helper Suite 没装好。

处理：

- 用 Manager 重新安装 `ComfyUI-VideoHelperSuite`；
- 重启 ComfyUI；
- 看终端有没有缺包报错。

### 10.2 找不到 SVD_img2vid_Conditioning

说明 ComfyUI 版本太旧，或节点搜索名字不对。

处理：

- 确认 ComfyUI 是 `v0.22.0`；
- 右键空白处，搜索 `SVD`；
- 如果仍找不到，更新或重新安装 ComfyUI。

### 10.3 视频动得太厉害

把：

```text
motion_bucket_id
```

降低到：

```text
80 到 110
```

### 10.4 视频几乎不动

把：

```text
motion_bucket_id
```

提高到：

```text
150 到 180
```

### 10.5 视频和原图差太多

把：

```text
augmentation_level
```

保持在：

```text
0.00 到 0.05
```

不要调太高。

## 11. 最简单总结

记住这个流程就够了：

```text
装 Video Helper Suite
下载 svd_xt.safetensors
放到 models\checkpoints
准备一张 1024x576 图片
加载 SVD 图片转视频工作流
video_frames 设置 14
fps 设置 6
点击 Queue Prompt
去 output 文件夹拿视频
```

## 12. 参考来源

- ComfyUI Releases：`https://github.com/Comfy-Org/ComfyUI/releases`
- Stable Video Diffusion XT：`https://huggingface.co/stabilityai/stable-video-diffusion-img2vid-xt`
- Stable Video Diffusion：`https://huggingface.co/stabilityai/stable-video-diffusion-img2vid`
- ComfyUI-VideoHelperSuite：`https://github.com/Kosinkadink/ComfyUI-VideoHelperSuite`
