import { ArrowDownIcon, ArrowUpIcon, ArrowUpDownIcon, EllipsisVerticalIcon, TrophyIcon } from "lucide-react"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { enumLabel } from "@/lib/enums"
import { formatDate } from "@/lib/format"

const EMPTY = <span className="text-muted-foreground">—</span>

/**
 * Колонки таблицы. meta.field — поле API: по нему колонка сортируется и фильтруется.
 * actions: { onOpen, onEdit, onDelete }.
 */
export function movieColumns(actions) {
  return [
    {
      id: "id",
      header: "ID",
      meta: { field: "id", className: "w-16" },
      cell: ({ row }) => <span className="tabular-nums text-muted-foreground">{row.original.id}</span>,
    },
    {
      id: "name",
      header: "Название",
      meta: { field: "name" },
      cell: ({ row }) => (
        <Button variant="link" className="h-auto p-0 text-left font-medium text-foreground" onClick={() => actions.onOpen(row.original)}>
          {row.original.name}
        </Button>
      ),
    },
    {
      id: "coordinates.x",
      header: "X",
      meta: { field: "coordinates.x" },
      cell: ({ row }) => <span className="tabular-nums">{row.original.coordinates.x}</span>,
    },
    {
      id: "coordinates.y",
      header: "Y",
      meta: { field: "coordinates.y" },
      cell: ({ row }) => <span className="tabular-nums">{row.original.coordinates.y}</span>,
    },
    {
      id: "creationDate",
      header: "Создан",
      meta: { field: "creationDate" },
      cell: ({ row }) => <span className="whitespace-nowrap">{formatDate(row.original.creationDate)}</span>,
    },
    {
      id: "oscarsCount",
      header: "Оскары",
      meta: { field: "oscarsCount" },
      cell: ({ row }) => (
        <span className="inline-flex items-center gap-1 tabular-nums">
          <TrophyIcon className="size-3.5 text-muted-foreground" />
          {row.original.oscarsCount}
        </span>
      ),
    },
    {
      id: "genre",
      header: "Жанр",
      meta: { field: "genre" },
      cell: ({ row }) => row.original.genre
        ? <Badge variant="secondary">{enumLabel("genres", row.original.genre)}</Badge>
        : EMPTY,
    },
    {
      id: "mpaaRating",
      header: "Рейтинг",
      meta: { field: "mpaaRating" },
      cell: ({ row }) => <Badge variant="outline">{enumLabel("mpaa-ratings", row.original.mpaaRating)}</Badge>,
    },
    {
      id: "screenwriter.name",
      header: "Сценарист",
      meta: { field: "screenwriter.name" },
      cell: ({ row }) => row.original.screenwriter?.name ?? EMPTY,
    },
    {
      id: "screenwriter.passportID",
      header: "Паспорт",
      meta: { field: "screenwriter.passportID" },
      cell: ({ row }) => row.original.screenwriter?.passportID ?? EMPTY,
    },
    {
      id: "screenwriter.hairColor",
      header: "Цвет волос",
      meta: { field: "screenwriter.hairColor" },
      cell: ({ row }) => enumLabel("colors", row.original.screenwriter?.hairColor) ?? EMPTY,
    },
    {
      id: "screenwriter.nationality",
      header: "Национальность",
      meta: { field: "screenwriter.nationality" },
      cell: ({ row }) => enumLabel("countries", row.original.screenwriter?.nationality) ?? EMPTY,
    },
    {
      id: "actions",
      header: () => <span className="sr-only">Действия</span>,
      enableHiding: false,
      meta: { className: "w-10" },
      cell: ({ row }) => (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon-sm" className="text-muted-foreground" aria-label="Действия">
              <EllipsisVerticalIcon />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-40">
            <DropdownMenuItem onClick={() => actions.onOpen(row.original)}>Подробнее</DropdownMenuItem>
            <DropdownMenuItem onClick={() => actions.onEdit(row.original)}>Изменить</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem variant="destructive" onClick={() => actions.onDelete(row.original)}>Удалить</DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      ),
    },
  ]
}

/** Заголовок с сортировкой: стрелка направления и номер приоритета при сортировке по нескольким полям. */
export function SortableHeader({ label, field, sorts, onToggle }) {
  const index = sorts.findIndex((s) => s.field === field)
  const sort = sorts[index]
  const Icon = !sort ? ArrowUpDownIcon : sort.direction === "asc" ? ArrowUpIcon : ArrowDownIcon
  return (
    <Button variant="ghost" size="sm" className="h-7 gap-1 px-1.5" onClick={() => onToggle(field)}>
      {label}
      <Icon className={sort ? "text-foreground" : "text-muted-foreground/60"} />
      {sort && sorts.length > 1 && <span className="text-xs text-muted-foreground">{index + 1}</span>}
    </Button>
  )
}
