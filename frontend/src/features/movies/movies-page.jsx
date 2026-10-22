import { Fragment, useMemo, useState } from "react"
import { PlusIcon, RotateCwIcon, Trash2Icon } from "lucide-react"
import { useMatch, useNavigate } from "react-router"

import { Button } from "@/components/ui/button"
import { ActiveFilters, ActiveSorts } from "./query-tags"
import { DeleteByGenreDialog } from "./delete-by-genre-dialog"
import { DeleteMovieDialog } from "./delete-movie-dialog"
import { MovieDetailsSheet } from "./movie-details-sheet"
import { MovieFormDialog } from "./movie-form-dialog"
import { MoviePagination } from "./movie-pagination"
import { MoviesTable } from "./movies-table"
import { useMovieList } from "./use-movie-list"

/** Раздел «Фильмы»: все операции Movie Service. */
export function MoviesPage() {
  const list = useMovieList()
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState(null)
  // открытая карточка — часть адреса: /movies/42
  const navigate = useNavigate()
  const movieIdParam = useMatch("/movies/:movieId")?.params.movieId
  const openedId = movieIdParam === undefined ? null : Number(movieIdParam)
  const closeMovie = () => navigate("/movies")
  const [deleting, setDeleting] = useState(null)
  const [deleteByGenreOpen, setDeleteByGenreOpen] = useState(false)

  const openCreate = () => {
    setEditing(null)
    setFormOpen(true)
  }

  const actions = useMemo(() => ({
    onOpen: (movie) => navigate(`/movies/${movie.id}`),
    onEdit: (movie) => {
      setEditing(movie)
      setFormOpen(true)
    },
    onDelete: (movie) => setDeleting(movie),
  }), [navigate])

  return (
    <div className="flex flex-col gap-4 px-4 py-4 md:py-6 lg:px-6">
      <MoviesTable
        movies={list.data.items}
        loading={list.loading}
        sorts={list.sorts}
        onSortsChange={list.setSorts}
        filters={list.filters}
        filterErrors={list.paramErrors}
        onFiltersChange={list.setFilters}
        actions={actions}
        toolbarStart={
          <>
            <ActiveFilters filters={list.filters} errors={list.paramErrors} onChange={list.setFilters} />
            <ActiveSorts sorts={list.sorts} errors={list.paramErrors} onChange={list.setSorts} />
          </>
        }
        toolbarActions={
          <>
            <Button variant="outline" size="sm" onClick={list.reload} aria-label="Обновить">
              <RotateCwIcon />
            </Button>
            <Button size="sm" onClick={openCreate}>
              <PlusIcon />
              Добавить фильм
            </Button>
          </>
        }
        toolbarEnd={
          <Button variant="outline" size="sm" onClick={() => setDeleteByGenreOpen(true)}>
            <Trash2Icon />
            Удалить по жанру
          </Button>
        }
        emptyAction={<Button size="sm" onClick={openCreate}><PlusIcon />Добавить фильм</Button>}
      />

      <MoviePagination
        page={list.page}
        size={list.size}
        totalElements={list.data.totalElements}
        totalPages={list.data.totalPages}
        onPageChange={list.setPage}
        onSizeChange={list.setSize}
      />

      {/* полный запрос; переносится по границам параметров (после &), длинное значение — где угодно */}
      <code className="rounded-md bg-muted px-2 py-1 text-xs leading-relaxed text-muted-foreground [overflow-wrap:anywhere]">
        GET /movies?
        {decodeURIComponent(list.requestQuery).split("&").map((param, i) => (
          <Fragment key={i}>
            {i > 0 && <>&amp;<wbr /></>}
            {param}
          </Fragment>
        ))}
      </code>

      <MovieFormDialog
        open={formOpen}
        movie={editing}
        onOpenChange={setFormOpen}
        onSaved={() => list.reload()}
      />
      <MovieDetailsSheet
        movieId={openedId}
        onOpenChange={(open) => !open && closeMovie()}
        onEdit={(movie) => {
          closeMovie()
          actions.onEdit(movie)
        }}
        onDelete={(movie) => {
          closeMovie()
          setDeleting(movie)
        }}
      />
      <DeleteMovieDialog
        movie={deleting}
        onOpenChange={(open) => !open && setDeleting(null)}
        onDeleted={() => list.reload()}
      />
      <DeleteByGenreDialog
        open={deleteByGenreOpen}
        onOpenChange={setDeleteByGenreOpen}
        onDeleted={() => list.reload()}
      />
    </div>
  )
}
