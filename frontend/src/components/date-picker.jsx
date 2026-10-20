import { useEffect, useState } from "react"
import { CalendarIcon } from "lucide-react"
import { ru } from "react-day-picker/locale"

import { Button } from "@/components/ui/button"
import { Calendar } from "@/components/ui/calendar"
import { Input } from "@/components/ui/input"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { maskDate, parseMaskedDate, toMaskedDate } from "@/lib/format"
import { cn } from "@/lib/utils"

/**
 * Дата: ввод с маской ДД/ММ/ГГГГ или выбор в календаре (shadcn Date Picker with input).
 * value и onChange — дата в формате API "YYYY-MM-DD" или "" (не задана).
 * onChange вызывается, когда дата введена полностью и существует, либо поле очищено.
 */
export function DatePicker({ value, onChange, invalid = false, className, "aria-label": ariaLabel }) {
  const [open, setOpen] = useState(false)
  const [text, setText] = useState(() => toMaskedDate(value))
  const textInvalid = text.length === 10 && !parseMaskedDate(text)

  // значение поменялось снаружи; недописанную дату пользователя не трогаем
  useEffect(() => setText((current) => (parseMaskedDate(current) === value ? current : toMaskedDate(value))), [value])

  const type = (raw) => {
    const masked = maskDate(raw)
    setText(masked)
    if (masked === "") onChange("")
    const iso = parseMaskedDate(masked)
    if (iso) onChange(iso)
  }

  const pick = (date) => {
    if (!date) return
    const iso = [date.getFullYear(), String(date.getMonth() + 1).padStart(2, "0"), String(date.getDate()).padStart(2, "0")].join("-")
    setText(toMaskedDate(iso))
    onChange(iso)
    setOpen(false)
  }

  const selected = value ? new Date(`${value}T00:00:00`) : undefined

  return (
    <div className={cn("relative", className)}>
      <Input
        value={text}
        onChange={(e) => type(e.target.value)}
        placeholder="ДД/ММ/ГГГГ"
        inputMode="numeric"
        aria-label={ariaLabel}
        aria-invalid={invalid || textInvalid || undefined}
        className="h-7 pr-8 text-xs tabular-nums"
      />
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            type="button"
            variant="ghost"
            size="icon-xs"
            className="absolute top-1/2 right-1 -translate-y-1/2 text-muted-foreground"
            aria-label="Открыть календарь"
          >
            <CalendarIcon />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-auto p-0" align="end">
          <Calendar
            mode="single"
            locale={ru}
            captionLayout="dropdown"
            selected={selected}
            defaultMonth={selected}
            onSelect={pick}
          />
        </PopoverContent>
      </Popover>
    </div>
  )
}
