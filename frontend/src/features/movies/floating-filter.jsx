import { useEffect, useState } from "react"
import { SearchIcon } from "lucide-react"

import { DatePicker } from "@/components/date-picker"
import { EnumCombobox } from "@/components/enum-combobox"
import { OperatorIcon } from "@/components/operator-icon"
import { Input } from "@/components/ui/input"
import { describeFilter, field, fieldFilters, filterValueLabel, replaceFieldFilters, requiresValue } from "@/lib/movie-query"

const APPLY_DELAY_MS = 400

const QUICK_OPERATOR = { string: "contains", integer: "eq", long: "eq", date: "eq", enum: "eq" }

const NUMBER_INPUT = {
  inputMode: "numeric",
  placeholder: "Поиск",
  parse: (text) => {
    const t = text.trim()
    return t === "" || /^-?\d+$/.test(t) ? { value: t } : { invalid: true }
  },
}

const QUICK_INPUTS = {
  string: { placeholder: "Поиск", parse: (text) => ({ value: text.trim() }) },
  integer: NUMBER_INPUT,
  long: NUMBER_INPUT,
}

export function FloatingFilter({ fieldKey, filters, onApply }) {
  const meta = field(fieldKey)
  const quickOperator = QUICK_OPERATOR[meta.type]
  const applied = fieldFilters(filters, fieldKey)
  const isQuick = (f) => f.operator === quickOperator || (meta.type === "enum" && f.operator === "isNull")
  const complex = applied.length > 1 || (applied.length === 1 && !isQuick(applied[0]))

  const apply = (next) => onApply(replaceFieldFilters(filters, fieldKey, next))

  if (complex) {
    const summary = applied.map(describeFilter).join("; ")
    return (
      <div
        role="note"
        title={summary}
        aria-label={`Фильтр: ${summary}`}
        className="flex h-7 min-w-24 items-center gap-2 overflow-hidden rounded-md border bg-muted/50 px-2 text-xs whitespace-nowrap text-muted-foreground"
      >
        {applied.map((f) => (
          <span key={f.index} className="inline-flex items-center gap-0.5">
            <OperatorIcon operator={f.operator} className="size-3" />
            {requiresValue(f.operator) && filterValueLabel(f)}
          </span>
        ))}
      </div>
    )
  }

  if (meta.type === "enum") {
    const current = applied[0]
    const value = current ? (current.operator === "isNull" ? null : current.value) : undefined
    return (
      <EnumCombobox
        dictionary={meta.dictionary}
        value={value}
        nullable={meta.nullable}
        nullLabel="(Пусто)"
        clearLabel="Все"
        placeholder="Все"
        aria-label={`Фильтр: ${meta.label}`}
        className="h-7 min-w-24 text-xs"
        onChange={(v) => apply(v === undefined ? [] : v === null ? [{ operator: "isNull" }] : [{ operator: "eq", value: v }])}
      />
    )
  }

  if (meta.type === "date") {
    return (
      <DatePicker
        value={applied[0]?.value ?? ""}
        onChange={(v) => apply(v === "" ? [] : [{ operator: quickOperator, value: v }])}
        aria-label={`Фильтр: ${meta.label}`}
        className="min-w-32"
      />
    )
  }

  return (
    <QuickInput
      meta={meta}
      value={applied[0]?.value ?? ""}
      onApply={(v) => apply(v === "" ? [] : [{ operator: quickOperator, value: v }])}
    />
  )
}

function QuickInput({ meta, value, onApply }) {
  const { parse, ...inputProps } = QUICK_INPUTS[meta.type]
  const [text, setText] = useState(value)
  const parsed = parse(text)

  useEffect(() => setText((current) => (parse(current).value === value ? current : value)), [value])

  useEffect(() => {
    if (parsed.invalid || parsed.value === value) return
    const timer = setTimeout(() => onApply(parsed.value), APPLY_DELAY_MS)
    return () => clearTimeout(timer)
  }, [parsed.invalid, parsed.value, value, onApply])

  return (
    <div className="relative">
      <SearchIcon className="pointer-events-none absolute top-1/2 left-2 size-3.5 -translate-y-1/2 text-muted-foreground" />
      <Input
        type="text"
        {...inputProps}
        value={text}
        onChange={(e) => setText(e.target.value)}
        aria-label={`Фильтр: ${meta.label}`}
        aria-invalid={parsed.invalid || undefined}
        className="h-7 min-w-24 pl-7 text-xs"
      />
    </div>
  )
}
