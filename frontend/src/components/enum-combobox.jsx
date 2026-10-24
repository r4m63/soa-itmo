import { useState } from "react"
import { CheckIcon, ChevronsUpDownIcon } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from "@/components/ui/command"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { useDictionarySearch } from "@/hooks/use-dictionary-search"
import { enumLabel } from "@/lib/enums"
import { cn } from "@/lib/utils"

export function EnumCombobox({
  dictionary,
  value,
  onChange,
  nullable = false,
  nullLabel = "Не указано",
  placeholder = "Выберите…",
  invalid = false,
  clearLabel,
  id,
  className,
  "aria-label": ariaLabel,
}) {
  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState("")
  const { items, loading } = useDictionarySearch(dictionary, search, open)

  const select = (next) => {
    onChange(next)
    setOpen(false)
    setSearch("")
  }

  const display = value ? enumLabel(dictionary, value) : nullable && value === null ? nullLabel : null

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          id={id}
          type="button"
          variant="outline"
          role="combobox"
          aria-expanded={open}
          aria-label={ariaLabel}
          aria-invalid={invalid || undefined}
          className={cn("w-full justify-between font-normal", className)}
        >
          {display ?? <span className="truncate text-muted-foreground">{placeholder}</span>}
          <ChevronsUpDownIcon className="opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-(--radix-popover-trigger-width) min-w-48 p-0" align="start">
        <Command shouldFilter={false}>
          <CommandInput placeholder="Поиск…" value={search} onValueChange={setSearch} />
          <CommandList>
            <CommandEmpty>{loading ? "Поиск…" : "Ничего не найдено"}</CommandEmpty>
            {(clearLabel || nullable) && !search && (
              <CommandGroup>
                {clearLabel && (
                  <CommandItem value="__clear" onSelect={() => select(undefined)}>
                    <span className="text-muted-foreground">{clearLabel}</span>
                    <CheckIcon className={cn("ml-auto", value === undefined ? "opacity-100" : "opacity-0")} />
                  </CommandItem>
                )}
                {nullable && <CommandItem value="__null" onSelect={() => select(null)}>
                  <span className="text-muted-foreground">{nullLabel}</span>
                  <CheckIcon className={cn("ml-auto", value === null ? "opacity-100" : "opacity-0")} />
                </CommandItem>}
              </CommandGroup>
            )}
            <CommandGroup>
              {items.map((item) => (
                <CommandItem key={item} value={item} onSelect={() => select(item)}>
                  {enumLabel(dictionary, item)}
                  <CheckIcon className={cn("ml-auto", value === item ? "opacity-100" : "opacity-0")} />
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  )
}
