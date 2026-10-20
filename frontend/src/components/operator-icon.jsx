import {
  ArrowLeftToLineIcon,
  ArrowRightToLineIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  CircleDashedIcon,
  CircleDotIcon,
  EqualIcon,
  EqualNotIcon,
  TextSearchIcon,
} from "lucide-react"

import { OPERATORS } from "@/lib/movie-query"
import { cn } from "@/lib/utils"

/** Иконки lucide для операторов filter. Готовых ≥ и ≤ в lucide нет — они собраны из двух иконок. */
const ICONS = {
  eq: [EqualIcon],
  ne: [EqualNotIcon],
  gt: [ChevronRightIcon],
  gte: [ChevronRightIcon, EqualIcon],
  lt: [ChevronLeftIcon],
  lte: [ChevronLeftIcon, EqualIcon],
  contains: [TextSearchIcon],
  startsWith: [ArrowLeftToLineIcon],
  endsWith: [ArrowRightToLineIcon],
  isNull: [CircleDashedIcon],
  notNull: [CircleDotIcon],
}

export function OperatorIcon({ operator, className }) {
  return (
    <span className="inline-flex shrink-0 items-center -space-x-1" title={OPERATORS[operator]} aria-hidden>
      {ICONS[operator].map((Icon, i) => <Icon key={i} className={cn("size-3.5", className)} />)}
    </span>
  )
}
