import { ChevronLeftIcon, ChevronRightIcon, ChevronsLeftIcon, ChevronsRightIcon } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { MOVIES_WORDS, plural } from "@/lib/format"
import { PAGE_SIZES } from "@/lib/movie-query"

export function MoviePagination({ page, size, totalElements, totalPages, onPageChange, onSizeChange }) {
  const last = Math.max(totalPages - 1, 0)
  return (
    <div className="flex flex-wrap items-center justify-between gap-3 text-sm">
      <span className="text-muted-foreground">
        Всего {totalElements} {plural(totalElements, MOVIES_WORDS)}
      </span>
      <div className="flex flex-wrap items-center gap-4">
        <div className="flex items-center gap-2">
          <Label htmlFor="page-size" className="font-normal">На странице</Label>
          <Select value={String(size)} onValueChange={(v) => onSizeChange(Number(v))}>
            <SelectTrigger id="page-size" size="sm" className="w-20"><SelectValue /></SelectTrigger>
            <SelectContent side="top">
              {PAGE_SIZES.map((s) => <SelectItem key={s} value={String(s)}>{s}</SelectItem>)}
            </SelectContent>
          </Select>
        </div>
        <span className="tabular-nums">Страница {totalPages === 0 ? 0 : page + 1} из {totalPages}</span>
        <div className="flex items-center gap-1">
          <Button variant="outline" size="icon-sm" aria-label="Первая страница" disabled={page === 0} onClick={() => onPageChange(0)}>
            <ChevronsLeftIcon />
          </Button>
          <Button variant="outline" size="icon-sm" aria-label="Предыдущая страница" disabled={page === 0} onClick={() => onPageChange(page - 1)}>
            <ChevronLeftIcon />
          </Button>
          <Button variant="outline" size="icon-sm" aria-label="Следующая страница" disabled={page >= last} onClick={() => onPageChange(page + 1)}>
            <ChevronRightIcon />
          </Button>
          <Button variant="outline" size="icon-sm" aria-label="Последняя страница" disabled={page >= last} onClick={() => onPageChange(last)}>
            <ChevronsRightIcon />
          </Button>
        </div>
      </div>
    </div>
  )
}
