import { useEffect, useState } from "react"
import { ListFilterIcon, PlusIcon, SearchIcon, Trash2Icon } from "lucide-react"

import { DatePicker } from "@/components/date-picker"
import { EnumCombobox } from "@/components/enum-combobox"
import { OperatorIcon } from "@/components/operator-icon"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import { Input } from "@/components/ui/input"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Separator } from "@/components/ui/separator"
import { useDictionarySearch } from "@/hooks/use-dictionary-search"
import { enumLabel } from "@/lib/enums"
import { field, fieldFilters, OPERATORS, operatorsFor, replaceFieldFilters, requiresValue } from "@/lib/movie-query"
import { cn } from "@/lib/utils"

const SET_OPERATORS = ["eq", "isNull"]

const EMPTY_CONDITION = { operator: "", value: "" }

const APPLY_DELAY_MS = 400

function conditionState(meta, c) {
  if (!c.operator) return "pending"
  if (!requiresValue(c.operator)) return "ready"
  const value = String(c.value ?? "").trim()
  if (value === "") return "pending"
  if ((meta.type === "integer" || meta.type === "long") && !/^-?\d+$/.test(value)) return "invalid"
  return "ready"
}

function normalize({ operator, value }) {
  return requiresValue(operator) ? { operator, value: String(value).trim() } : { operator }
}

export function ColumnFilter({ fieldKey, filters, errors, onApply }) {
  const meta = field(fieldKey)
  const isEnum = meta.type === "enum"
  const applied = fieldFilters(filters, fieldKey)
  const appliedErrors = applied.map((f) => errors[`filter[${f.index}]`]).filter(Boolean)

  const [open, setOpen] = useState(false)
  const [draft, setDraft] = useState([])

  const ready = JSON.stringify(draft.filter((c) => conditionState(meta, c) === "ready").map(normalize))
  const current = JSON.stringify(applied.map(normalize))

  const onOpenChange = (next) => {
    if (next) {
      const existing = applied.map(({ operator, value }) => ({ operator, value }))
      setDraft(existing.length || isEnum ? existing : [EMPTY_CONDITION])
    } else if (ready !== current) {
      onApply(replaceFieldFilters(filters, fieldKey, JSON.parse(ready)))
    }
    setOpen(next)
  }

  const conditions = draft
    .map((c, i) => ({ ...c, i }))
    .filter((c) => !isEnum || !SET_OPERATORS.includes(c.operator))
  const conditionOperators = operatorsFor(fieldKey).filter((op) => !isEnum || !SET_OPERATORS.includes(op))

  const update = (i, patch) => setDraft((d) => d.map((c, j) => (j === i ? { ...c, ...patch } : c)))

  useEffect(() => {
    if (!open || ready === current) return
    const timer = setTimeout(() => onApply(replaceFieldFilters(filters, fieldKey, JSON.parse(ready))), APPLY_DELAY_MS)
    return () => clearTimeout(timer)
  }, [open, ready, current, filters, fieldKey, onApply])

  const reset = () => {
    setDraft(isEnum ? [] : [EMPTY_CONDITION])
    onApply(replaceFieldFilters(filters, fieldKey, []))
  }

  return (
    <Popover open={open} onOpenChange={onOpenChange}>
      <PopoverTrigger asChild>
        <Button
          variant={applied.length ? "secondary" : "ghost"}
          size="icon-xs"
          className={cn(
            "relative shrink-0",
            !applied.length && "text-muted-foreground/60",
            appliedErrors.length && "text-destructive"
          )}
          aria-label={`Фильтр: ${meta.label}`}
        >
          <ListFilterIcon />
          {applied.length > 0 && (
            <span className="absolute -top-1 -right-1 flex size-3.5 items-center justify-center rounded-full bg-primary text-[0.6rem] text-primary-foreground">
              {applied.length}
            </span>
          )}
        </Button>
      </PopoverTrigger>
      <PopoverContent align="start" className="w-96 p-3">
        <div className="flex flex-col gap-3">
          <p className="text-sm font-medium">{meta.label}</p>

          {isEnum && <EnumSetFilter meta={meta} draft={draft} setDraft={setDraft} />}

          {isEnum && <Separator />}
          {isEnum && <p className="text-xs text-muted-foreground">Сравнения</p>}
          {conditions.map((c) => (
            <Condition
              key={c.i}
              meta={meta}
              operators={conditionOperators}
              condition={c}
              invalid={conditionState(meta, c) === "invalid"}
              error={c.i < applied.length ? errors[`filter[${applied[c.i]?.index}]`] : undefined}
              onChange={(patch) => update(c.i, patch)}
              onRemove={() => setDraft((d) => d.filter((_, j) => j !== c.i))}
            />
          ))}
          <Button
            variant="ghost"
            size="sm"
            className="self-start"
            onClick={() => setDraft((d) => [...d, EMPTY_CONDITION])}
          >
            <PlusIcon />
            Добавить условие
          </Button>

          <div className="flex justify-end gap-2">
            <Button variant="outline" size="sm" onClick={reset} disabled={!applied.length}>Сбросить</Button>
          </div>
        </div>
      </PopoverContent>
    </Popover>
  )
}

function EnumSetFilter({ meta, draft, setDraft }) {
  const [search, setSearch] = useState("")
  const { items } = useDictionarySearch(meta.dictionary, search, true)

  const checked = (operator, value) => draft.some((c) => c.operator === operator && (operator === "isNull" || c.value === value))
  const toggle = (operator, value) => setDraft((d) => checked(operator, value)
    ? d.filter((c) => !(c.operator === operator && (operator === "isNull" || c.value === value)))
    : [...d, operator === "isNull" ? { operator } : { operator, value }])

  return (
    <div className="flex flex-col gap-2">
      <div className="relative">
        <SearchIcon className="pointer-events-none absolute top-1/2 left-2 size-3.5 -translate-y-1/2 text-muted-foreground" />
        <Input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Поиск" aria-label="Поиск значений" className="h-7 pl-7" />
      </div>
      <div className="flex max-h-48 flex-col gap-1 overflow-y-auto">
        {meta.nullable && !search && (
          <SetOption label="(Пусто)" checked={checked("isNull")} onToggle={() => toggle("isNull")} muted />
        )}
        {items.map((value) => (
          <SetOption key={value} label={enumLabel(meta.dictionary, value)}
                     checked={checked("eq", value)} onToggle={() => toggle("eq", value)} />
        ))}
        {items.length === 0 && <p className="px-1 text-sm text-muted-foreground">Ничего не найдено</p>}
      </div>
    </div>
  )
}

function SetOption({ label, checked, onToggle, muted }) {
  return (
    <label className="flex cursor-pointer items-center gap-2 rounded-md px-1 py-1 text-sm hover:bg-muted">
      <Checkbox checked={checked} onCheckedChange={onToggle} aria-label={label} />
      <span className={cn(muted && "text-muted-foreground")}>{label}</span>
    </label>
  )
}

function Condition({ meta, operators, condition, invalid, error, onChange, onRemove }) {
  return (
    <div className="flex flex-col gap-1">
      <div className="flex items-center gap-2">
        <Select
          value={condition.operator}
          onValueChange={(operator) => onChange({ operator, value: requiresValue(operator) ? condition.value : undefined })}
        >
          <SelectTrigger size="sm" className="w-44 shrink-0" aria-label="Условие"><SelectValue placeholder="Условие" /></SelectTrigger>
          <SelectContent>
            {operators.map((op) => (
              <SelectItem key={op} value={op}>
                <OperatorIcon operator={op} />
                {OPERATORS[op]}
              </SelectItem>
            ))}
          </SelectContent>
        </Select>
        <div className="min-w-0 flex-1">
          {!condition.operator ? (
            <Input disabled aria-label="Значение" className="h-7" />
          ) : requiresValue(condition.operator) && (
            <ConditionValue meta={meta} value={condition.value} invalid={invalid} onChange={(value) => onChange({ value })} />
          )}
        </div>
        <Button variant="ghost" size="icon-sm" aria-label="Удалить условие" onClick={onRemove}>
          <Trash2Icon />
        </Button>
      </div>
      {error && <p className="text-xs text-destructive">{error}</p>}
    </div>
  )
}

function ConditionValue({ meta, value, invalid, onChange }) {
  if (meta.type === "enum") {
    return (
      <EnumCombobox dictionary={meta.dictionary} value={value || undefined} onChange={(v) => onChange(v ?? "")}
                    invalid={invalid} placeholder="" className="h-7" />
    )
  }
  if (meta.type === "date") {
    return <DatePicker value={value ?? ""} onChange={onChange} invalid={invalid} aria-label="Значение" />
  }
  const props = {
    integer: { type: "number", step: 1 },
    long: { type: "number", step: 1 },
    string: { type: "text" },
  }[meta.type]
  return (
    <Input {...props} aria-label="Значение" className="h-7" value={value ?? ""}
           aria-invalid={invalid || undefined} onChange={(e) => onChange(e.target.value)} />
  )
}
