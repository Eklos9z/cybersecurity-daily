#!/bin/bash
# ============================================
# 网安日报速递 - 一键部署脚本
# 用法: bash deploy.sh [commit message]
# ============================================

set -e

PROJECT_DIR="C:/Users/UncleC/Desktop/cybersecurity-daily"
REPORTS_DIR="C:/Users/UncleC/WorkBuddy/automation-2026-05-13-task-1/reports"
PYTHON="C:/Users/UncleC/.workbuddy/binaries/python/versions/3.13.12/python.exe"

cd "$PROJECT_DIR"

echo "==> [1/5] 复制最新HTML日报..."
# 查找reports目录中最新的HTML文件
latest_html=$(ls -t "$REPORTS_DIR"/*.html 2>/dev/null | head -1)

if [ -z "$latest_html" ]; then
    echo "    未找到HTML日报文件，跳过复制"
else
    filename=$(basename "$latest_html")
    # 从中文文件名提取日期并转为 YYYY-MM-DD 格式
    converted=$($PYTHON -c "
import re, sys
m = re.search(r'(\d{4})\u5e74(\d{1,2})\u6708(\d{1,2})\u65e5', '$filename')
if m:
    print(f'{m.group(1)}-{int(m.group(2)):02d}-{int(m.group(3)):02d}.html')
else:
    print('$filename')
" 2>/dev/null || echo "$filename")

    cp "$latest_html" "daily/$converted"
    echo "    复制: $filename -> daily/$converted"
fi

echo "==> [2/5] 更新 index.json 清单..."
# 扫描 daily/ 目录下的 HTML 文件，自动生成 index.json
$PYTHON -c "
import os, json, re

daily_dir = 'daily'
entries = []

for f in sorted(os.listdir(daily_dir)):
    if not f.endswith('.html') or f == 'index.html':
        continue
    
    # 从文件名提取日期 2026-06-04.html -> 2026-06-04
    date_match = re.match(r'(\d{4}-\d{2}-\d{2})\.html', f)
    if not date_match:
        continue
    
    date_str = date_match.group(1)
    
    # 尝试从HTML文件中提取关键词
    filepath = os.path.join(daily_dir, f)
    keywords = ''
    try:
        with open(filepath, 'r', encoding='utf-8') as fh:
            content = fh.read()
            # 查找 keywords-banner 或 keywords-banner 内容
            kw_match = re.search(r'keywords-banner[^>]*>(.*?)</div>', content, re.DOTALL)
            if kw_match:
                keywords = re.sub(r'<[^>]+>', '', kw_match.group(1)).strip()
                # 去掉常见前缀
                keywords = re.sub(r'^最新一期[：:]\s*', '', keywords)
                keywords = re.sub(r'^今日关键词[：:]\s*', '', keywords)
                keywords = re.sub(r'^\d{4}年\d{1,2}月\d{1,2}日\s*[—–-]\s*', '', keywords)
            else:
                # 备选：从 title 或 h1 提取
                title_match = re.search(r'<title>(.*?)</title>', content)
                if title_match:
                    keywords = title_match.group(1).strip()
    except Exception:
        pass
    
    # 计算星期
    from datetime import datetime
    weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
    try:
        d = datetime.strptime(date_str, '%Y-%m-%d')
        weekday = weekdays[d.weekday() + 1] if d.weekday() < 6 else weekdays[0]
    except Exception:
        weekday = ''
    
    entries.append({
        'date': date_str,
        'weekday': weekday,
        'keywords': keywords
    })

# 按日期降序
entries.sort(key=lambda x: x['date'], reverse=True)

with open(os.path.join(daily_dir, 'index.json'), 'w', encoding='utf-8') as out:
    json.dump(entries, out, ensure_ascii=False, indent=2)

print(f'    已更新 index.json ({len(entries)} 条记录)')
"

echo "==> [3/5] 检查Git状态..."
if [ ! -d ".git" ]; then
    echo "    Git仓库未初始化！请先运行:"
    echo "    cd $PROJECT_DIR && git init && git remote add origin <你的仓库URL>"
    exit 1
fi

changed=$(git status --porcelain | wc -l)
if [ "$changed" -eq 0 ]; then
    echo "    没有变更，无需提交"
    exit 0
fi

echo "==> [4/5] 提交变更..."
msg="${1:-daily update $(date +%Y-%m-%d)}"
git add -A
git commit -m "$msg"
echo "    提交: $msg"

echo "==> [5/5] 推送到远程..."
git push origin main
echo "    推送完成！"

echo ""
echo "✅ 部署成功！访问 https://unclecheng-li.github.io/cybersecurity-daily/ 查看更新"
