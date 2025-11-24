from PIL import Image
import os
from collections import defaultdict

# 定义RGB颜色值
COLORS = {
    'r': (255, 0, 0),    # 纯红色 #ff0000
    'g': (0, 255, 0),    # 纯绿色 #00ff00
    'b': (0, 0, 255)     # 纯蓝色 #0000ff
}

def extract_color_pixels(img, target_color, tolerance=10):
    """
    提取图片中目标颜色的像素，其他像素设为透明
    tolerance: 颜色容差，允许轻微的颜色偏差
    """
    # 转换为RGBA模式（包含透明通道）
    img_rgba = img.convert('RGBA')
    pixels = img_rgba.load()
    width, height = img_rgba.size
    
    # 创建透明背景的新图片
    result = Image.new('RGBA', (width, height), (0, 0, 0, 0))
    result_pixels = result.load()
    
    # 遍历所有像素
    for y in range(height):
        for x in range(width):
            r, g, b, a = pixels[x, y]
            # 检查像素颜色是否接近目标颜色
            if (abs(r - target_color[0]) < tolerance and
                abs(g - target_color[1]) < tolerance and
                abs(b - target_color[2]) < tolerance):
                # 保留目标颜色，不透明
                result_pixels[x, y] = (*target_color, 255)
    
    return result

def merge_images(r_img, g_img, b_img):
    """
    按R->G->B顺序合并图片，后者覆盖前者
    """
    # 获取图片尺寸
    width, height = r_img.size
    
    # 创建透明底图
    base = Image.new('RGBA', (width, height), (0, 0, 0, 0))
    
    # 按顺序粘贴：先R，再G（覆盖R），最后B（覆盖G和R）
    base.paste(r_img, (0, 0), r_img)  # 第三个参数是mask，用于处理透明
    base.paste(g_img, (0, 0), g_img)
    base.paste(b_img, (0, 0), b_img)
    
    return base

def process_directory(directory):
    """
    处理目录中的所有图片组
    """
    # 获取所有png文件
    files = [f for f in os.listdir(directory) if f.lower().endswith('.png')]
    
    # 按前缀分组
    groups = defaultdict(list)
    for f in files:
        # 检查后缀并提取前缀
        if f.lower().endswith('_r.png'):
            prefix = f[:-6]  # 去掉 "_r.png"
            groups[prefix].append('r')
        elif f.lower().endswith('_g.png'):
            prefix = f[:-6]  # 去掉 "_g.png"
            groups[prefix].append('g')
        elif f.lower().endswith('_b.png'):
            prefix = f[:-6]  # 去掉 "_b.png"
            groups[prefix].append('b')
    
    # 处理完整的组（必须包含r,g,b）
    for prefix, colors in groups.items():
        if set(colors) == {'r', 'g', 'b'}:  # 确保三色齐全
            try:
                print(f"\n处理组: {prefix}")
                
                # 构建文件路径
                r_path = os.path.join(directory, f"{prefix}_r.png")
                g_path = os.path.join(directory, f"{prefix}_g.png")
                b_path = os.path.join(directory, f"{prefix}_b.png")
                
                # 加载图片
                r_img = Image.open(r_path)
                g_img = Image.open(g_path)
                b_img = Image.open(b_path)
                
                # 验证图片尺寸一致
                if not (r_img.size == g_img.size == b_img.size):
                    print(f"  ❌ 错误：图片尺寸不一致")
                    continue
                
                # 提取纯色像素
                print("  提取红色通道...")
                r_extracted = extract_color_pixels(r_img, COLORS['r'])
                
                print("  提取绿色通道...")
                g_extracted = extract_color_pixels(g_img, COLORS['g'])
                
                print("  提取蓝色通道...")
                b_extracted = extract_color_pixels(b_img, COLORS['b'])
                
                # 合并图片
                print("  合并通道...")
                merged = merge_images(r_extracted, g_extracted, b_extracted)
                
                # 保存结果
                output_path = os.path.join(directory, f"{prefix}.png")
                merged.save(output_path)
                print(f"  ✅ 成功保存: {os.path.basename(output_path)}")
                
            except Exception as e:
                print(f"  ❌ 处理失败: {e}")
        else:
            print(f"\n跳过不完整的组: {prefix} (缺少颜色: {set(['r','g','b']) - set(colors)})")

def main():
    """主函数"""
    # 获取脚本所在目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    
    print("=" * 50)
    print("RGB颜色掩码合并工具")
    print("=" * 50)
    print(f"处理目录: {current_dir}")
    print("=" * 50)
    
    # 开始处理
    process_directory(current_dir)
    
    print("\n" + "=" * 50)
    print("所有处理完成！")
    print("=" * 50)

if __name__ == "__main__":
    main()
