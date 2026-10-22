import { Fragment, useMemo } from "react"
import { flexRender, getCoreRowModel, useReactTable } from "@tanstack/react-table"
import { Columns3Icon, FilmIcon } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
  ContextMenu,
  ContextMenuContent,
  ContextMenuItem,
  ContextMenuSeparator,
  ContextMenuTrigger,
} from "@/components/ui/context-menu"
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { Empty, EmptyDescription, EmptyHeader, EmptyMedia, EmptyTitle } from "@/components/ui/empty"
import { Skeleton } from "@/components/ui/skeleton"
import { useStoredState } from "@/hooks/use-stored-state"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { toggleSort } from "@/lib/movie-query"
import { cn } from "@/lib/utils"
import { ColumnFilter } from "./column-filter"
import { FloatingFilter } from "./floating-filter"
import { movieColumns, rowActions, SortableHeader } from "./movie-columns"

function sanitizeVisibility(stored) {
  if (!stored || typeof stored !== "object" || Array.isArray(stored)) return {}
  return Object.fromEntries(Object.entries(stored).filter(([, visible]) => typeof visible === "boolean"))
}

/**
 * Клик по строке открывает карточку фильма. Не срабатывает, если кликнули по кнопке в строке
 * (у неё своё действие), по пункту меню «⋮» (React доносит клики из портала до строки)
 * или если пользователь выделял текст.
 */
function openOnRowClick(event, open) {
  if (!event.currentTarget.contains(event.target)) return
  if (event.target.closest("button, a, input, [role=menuitem]")) return
  if (String(window.getSelection() ?? "").length > 0) return
  open()
}

/**
 * Таблица фильмов. Сортировка, фильтры и пагинация серверные: таблица только меняет sorts и filters,
 * данные приходят готовыми. В заголовке каждой колонки — сортировка и фильтр по её полю,
 * во второй строке заголовка — быстрый поиск по значению.
 * Над таблицей панель: слева toolbarStart (теги фильтров и сортировок) от самого верха,
 * справа две строки кнопок, прижатые к низу: сверху toolbarActions, снизу toolbarEnd и меню «Колонки».
 */
export function MoviesTable({
  movies, loading, sorts, onSortsChange, filters, filterErrors, onFiltersChange, actions, emptyAction,
  toolbarStart, toolbarActions, toolbarEnd,
}) {
  // скрытые колонки тоже запоминаются; неизвестные id просто не на что применить
  const [columnVisibility, setColumnVisibility] = useStoredState("movies:columns", {}, sanitizeVisibility)
  const columns = useMemo(() => movieColumns(actions), [actions])

  const table = useReactTable({
    data: movies,
    columns,
    state: { columnVisibility },
    onColumnVisibilityChange: setColumnVisibility,
    getRowId: (row) => String(row.id),
    getCoreRowModel: getCoreRowModel(),
    manualSorting: true,
    manualPagination: true,
  })

  const visibleColumns = table.getVisibleLeafColumns().length

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-wrap items-stretch justify-between gap-3">
        <div className="flex min-w-0 flex-1 flex-col gap-2">{toolbarStart}</div>
        <div className="ml-auto flex shrink-0 flex-col items-end gap-2 self-end">
          <div className="flex justify-end gap-2">{toolbarActions}</div>
          <div className="flex justify-end gap-2">
            {toolbarEnd}
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="outline" size="sm">
                  <Columns3Icon />
                  Колонки
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-44">
                {table.getAllColumns().filter((c) => c.getCanHide()).map((column) => (
                  <DropdownMenuCheckboxItem
                    key={column.id}
                    checked={column.getIsVisible()}
                    onCheckedChange={(value) => column.toggleVisibility(!!value)}
                    // меню остаётся открытым, чтобы можно было переключить несколько колонок подряд
                    onSelect={(e) => e.preventDefault()}
                  >
                    {column.columnDef.header}
                  </DropdownMenuCheckboxItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>

      <div className="overflow-hidden rounded-lg border">
        <Table>
          <TableHeader className="sticky top-0 z-10 bg-muted">
            {table.getHeaderGroups().map((group) => (
              <TableRow key={group.id}>
                {group.headers.map((header) => {
                  const meta = header.column.columnDef.meta ?? {}
                  return (
                    <TableHead key={header.id} className={meta.className}>
                      {meta.field ? (
                        <div className="flex items-center gap-0.5">
                          <SortableHeader
                            label={header.column.columnDef.header}
                            field={meta.field}
                            sorts={sorts}
                            onToggle={(key) => onSortsChange(toggleSort(sorts, key))}
                          />
                          <ColumnFilter fieldKey={meta.field} filters={filters} errors={filterErrors} onApply={onFiltersChange} />
                        </div>
                      ) : (
                        flexRender(header.column.columnDef.header, header.getContext())
                      )}
                    </TableHead>
                  )
                })}
              </TableRow>
            ))}
            <TableRow className="hover:bg-transparent">
              {table.getVisibleLeafColumns().map((column) => {
                const key = column.columnDef.meta?.field
                return (
                  <TableHead key={column.id} className="h-auto py-1.5">
                    {key && <FloatingFilter fieldKey={key} filters={filters} onApply={onFiltersChange} />}
                  </TableHead>
                )
              })}
            </TableRow>
          </TableHeader>
          <TableBody>
            {loading && movies.length === 0 ? (
              Array.from({ length: 6 }, (_, i) => (
                <TableRow key={i}>
                  <TableCell colSpan={visibleColumns}><Skeleton className="h-6 w-full" /></TableCell>
                </TableRow>
              ))
            ) : table.getRowModel().rows.length ? (
              table.getRowModel().rows.map((row) => (
                // правый клик по строке — те же действия, что в меню «⋮»
                <ContextMenu key={row.id}>
                  <ContextMenuTrigger asChild>
                    <TableRow
                      className={cn("cursor-pointer", loading && "opacity-60")}
                      onClick={(event) => openOnRowClick(event, () => actions.onOpen(row.original))}
                    >
                      {row.getVisibleCells().map((cell) => (
                        <TableCell key={cell.id} className={cell.column.columnDef.meta?.className}>
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </TableCell>
                      ))}
                    </TableRow>
                  </ContextMenuTrigger>
                  <ContextMenuContent className="w-40">
                    {rowActions(actions, row.original).map(({ label, run, destructive }) => (
                      <Fragment key={label}>
                        {destructive && <ContextMenuSeparator />}
                        <ContextMenuItem variant={destructive ? "destructive" : "default"} onSelect={run}>{label}</ContextMenuItem>
                      </Fragment>
                    ))}
                  </ContextMenuContent>
                </ContextMenu>
              ))
            ) : (
              <TableRow>
                <TableCell colSpan={visibleColumns}>
                  {/* по левому краю: таблица шире экрана, по центру сообщение уезжало бы за прокрутку */}
                  <Empty className="items-start py-10 text-left">
                    <EmptyHeader className="items-start">
                      <EmptyMedia variant="icon"><FilmIcon /></EmptyMedia>
                      <EmptyTitle>Фильмы не найдены</EmptyTitle>
                      <EmptyDescription>Измените фильтры в заголовках колонок или добавьте фильм.</EmptyDescription>
                    </EmptyHeader>
                    {emptyAction}
                  </Empty>
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
