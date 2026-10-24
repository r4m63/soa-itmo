import { useState } from "react"
import { closestCenter, DndContext, DragOverlay, KeyboardSensor, PointerSensor, useSensor, useSensors } from "@dnd-kit/core"
import {
  arrayMove,
  rectSortingStrategy,
  SortableContext,
  sortableKeyboardCoordinates,
  useSortable,
} from "@dnd-kit/sortable"
import { CSS } from "@dnd-kit/utilities"
import { ArrowDownIcon, ArrowDownUpIcon, ArrowUpIcon, GripVerticalIcon, ListFilterIcon, XIcon } from "lucide-react"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { OperatorIcon } from "@/components/operator-icon"
import { describeFilter, field, filterValueLabel, requiresValue } from "@/lib/movie-query"
import { cn } from "@/lib/utils"

export function ActiveFilters({ filters, errors, onChange }) {
  if (filters.length === 0) return null
  return (
    <TagRow icon={ListFilterIcon} label="Фильтры" onReset={() => onChange([])}>
      {filters.map((filter, index) => (
        <Tag
          key={index}
          text={describeFilter(filter)}
          label={<FilterLabel filter={filter} />}
          error={errors[`filter[${index}]`]}
          onRemove={() => onChange(filters.filter((_, i) => i !== index))}
        />
      ))}
    </TagRow>
  )
}

const SCREEN_READER_INSTRUCTIONS = {
  draggable: "Нажмите пробел, чтобы взять сортировку, стрелками поменяйте её приоритет, пробелом отпустите, Escape — отмена.",
}

export function ActiveSorts({ sorts, errors, onChange }) {
  const [activeId, setActiveId] = useState(null)
  const sensors = useSensors(
    useSensor(PointerSensor, { activationConstraint: { distance: 4 } }),
    useSensor(KeyboardSensor, { coordinateGetter: sortableKeyboardCoordinates }),
  )

  if (sorts.length === 0) return null

  const ids = sorts.map((s) => s.field)
  const tagText = (sort) => `${field(sort.field).label} ${sort.direction === "asc" ? "по возрастанию" : "по убыванию"}`
  const tagProps = (sort) => ({ label: field(sort.field).label, icon: sort.direction === "asc" ? ArrowUpIcon : ArrowDownIcon })
  const onDragEnd = ({ active, over }) => {
    setActiveId(null)
    if (!over || active.id === over.id) return
    onChange(arrayMove(sorts, ids.indexOf(active.id), ids.indexOf(over.id)))
  }
  const activeIndex = ids.indexOf(activeId)

  return (
    <TagRow icon={ArrowDownUpIcon} label="Сортировка" onReset={() => onChange([])}>
      <DndContext
        sensors={sensors}
        collisionDetection={closestCenter}
        onDragStart={({ active }) => setActiveId(active.id)}
        onDragEnd={onDragEnd}
        onDragCancel={() => setActiveId(null)}
        accessibility={{ screenReaderInstructions: SCREEN_READER_INSTRUCTIONS }}
      >
        <SortableContext items={ids} strategy={rectSortingStrategy}>
          {sorts.map((sort, index) => (
            <SortableTag
              key={sort.field}
              id={sort.field}
              priority={sorts.length > 1 ? index + 1 : null}
              text={tagText(sort)}
              {...tagProps(sort)}
              error={errors[`sort[${index}]`]}
              onRemove={() => onChange(sorts.filter((_, i) => i !== index))}
            />
          ))}
        </SortableContext>
        <DragOverlay dropAnimation={null}>
          {activeIndex >= 0 && (
            <Tag
              text={tagText(sorts[activeIndex])}
              {...tagProps(sorts[activeIndex])}
              className="cursor-grabbing shadow-md transition-none"
              prefix={<TagPrefix priority={sorts.length > 1 ? activeIndex + 1 : null} />}
            />
          )}
        </DragOverlay>
      </DndContext>
    </TagRow>
  )
}

function SortableTag({ id, priority, ...tag }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id })
  return (
    <Tag
      {...tag}
      ref={setNodeRef}
      style={{ transform: CSS.Translate.toString(transform), transition }}
      className={cn("cursor-grab touch-none transition-none", isDragging && "opacity-40")}
      handleProps={{ ...attributes, ...listeners, "aria-label": `Перетащить: ${tag.text}` }}
      prefix={<TagPrefix priority={priority} />}
    />
  )
}

function TagPrefix({ priority }) {
  return (
    <>
      <GripVerticalIcon className="size-3 text-muted-foreground" aria-hidden />
      {priority && <span className="text-muted-foreground tabular-nums">{priority}</span>}
    </>
  )
}

function FilterLabel({ filter }) {
  return (
    <>
      {field(filter.field).label}
      <OperatorIcon operator={filter.operator} className="size-3" />
      {requiresValue(filter.operator) && filterValueLabel(filter)}
    </>
  )
}

function TagRow({ icon: Icon, label, onReset, children }) {
  return (
    <div className="flex flex-wrap items-center gap-2" aria-label={label}>
      <Icon className="size-4 text-muted-foreground" aria-hidden />
      {children}
      <Button variant="ghost" size="sm" onClick={onReset}>Сбросить все</Button>
    </div>
  )
}

function Tag({ ref, text, label, icon: Icon, error, onRemove, prefix, handleProps, className, style }) {
  return (
    <Badge
      ref={ref}
      style={style}
      variant={error ? "destructive" : "secondary"}
      className={cn("h-7 gap-1 pr-1", prefix && "pl-1.5", className)}
      title={error}
      {...handleProps}
    >
      {prefix}
      {label ?? text}
      {Icon && <Icon className="size-3" aria-hidden />}
      <button
        type="button"
        className="cursor-pointer rounded-sm p-0.5 hover:bg-foreground/10"
        aria-label={`Убрать: ${text}`}
        onClick={onRemove}
        onPointerDown={(e) => e.stopPropagation()}
        onKeyDown={(e) => e.stopPropagation()}
      >
        <XIcon className="size-3" />
      </button>
    </Badge>
  )
}
