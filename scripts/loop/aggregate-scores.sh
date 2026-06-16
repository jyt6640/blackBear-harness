#!/usr/bin/env bash
# 철학 점수표 집계: history의 06-scorecard.md를 항목별로 통계 낸다.
# 낮은 항목 = 프롬프트(카드 지침 / 역할 docs / 정본) 개선 후보.
# 사용: scripts/loop/aggregate-scores.sh [--dir <history 경로>] [--bottom N]
# → docs/workflow/loop-engineering.md
set -u

ROOT="$(git rev-parse --show-toplevel 2>/dev/null || pwd)"
DIR="$ROOT/next-step/history"
BOTTOM=3

while [ "$#" -gt 0 ]; do
    case "$1" in
        --dir) DIR="$2"; shift 2 ;;
        --bottom) BOTTOM="$2"; shift 2 ;;
        *) echo "사용법: aggregate-scores.sh [--dir <경로>] [--bottom N]" >&2; exit 1 ;;
    esac
done

[ -d "$DIR" ] || { echo "FAIL: 디렉토리가 없다: $DIR" >&2; exit 1; }

python3 - "$DIR" "$BOTTOM" <<'PYEOF'
import sys, pathlib, re
from collections import defaultdict

base = pathlib.Path(sys.argv[1])
bottom_n = int(sys.argv[2])

cards = sorted(base.glob("*/06-scorecard.md"))
if not cards:
    print(f"집계할 점수표가 없다: {base}/*/06-scorecard.md")
    sys.exit(0)

# ID -> (항목, 출처, [점수...], NA수, [0/1점 근거(카드명)])
items = {}
stats = defaultdict(lambda: {"scores": [], "na": 0, "low": []})

row_re = re.compile(r"^\|\s*([A-Z]\d+)\s*\|")
for card in cards:
    task = card.parent.name
    for line in card.read_text().splitlines():
        m = row_re.match(line)
        if not m:
            continue
        cols = [c.strip() for c in line.strip().strip("|").split("|")]
        if len(cols) < 4:
            continue
        iid, name, source, score = cols[0], cols[1], cols[2], cols[3]
        items.setdefault(iid, (name, source))
        s = score.upper().replace(" ", "")
        if s in ("N/A", "NA"):
            stats[iid]["na"] += 1
        elif s in ("0", "1", "2"):
            stats[iid]["scores"].append(int(s))
            if int(s) < 2:
                stats[iid]["low"].append(f"{task}({s})")
        # 빈 점수는 미채점으로 무시

print(f"# 철학 점수 집계 (카드 {len(cards)}장: {', '.join(c.parent.name for c in cards)})")
print()
print("| ID | 항목 | 평균 | 채점수 | N/A | 감점 카드 |")
print("|---|---|---|---|---|---|")

ranked = []
for iid, (name, source) in items.items():
    sc = stats[iid]["scores"]
    if sc:
        avg = sum(sc) / len(sc)
        ranked.append((avg, iid))
        avg_s = f"{avg:.2f}"
    else:
        avg_s = "-"
    low = ", ".join(stats[iid]["low"]) or "-"
    print(f"| {iid} | {name[:30]} | {avg_s} | {len(sc)} | {stats[iid]['na']} | {low} |")

ranked.sort()
worst = [(a, i) for a, i in ranked if a < 2.0][:bottom_n]
print()
if worst:
    print(f"## 개선 후보 (평균 낮은 순, 상위 {len(worst)}개)")
    print()
    for avg, iid in worst:
        name, source = items[iid]
        print(f"- {iid} ({avg:.2f}): {name} → 출처: {source}")
    print()
    print("다음 단계: /loop-improve로 위 항목의 카드 지침 / 역할 docs / 정본 개선을 제안한다.")
else:
    print("모든 채점 항목이 만점이다.")
    print("점수가 계속 만점으로 고착되면 항목이 무뎌진 신호다. 점수표 항목 자체를 재검토한다.")
PYEOF
