import { useState } from 'react'
import { useTransactionHistory } from '../hooks/useTransactionHistory'
import { getIconConfig, getLabel, getAmountColor, getAmountPrefix, formatDate } from '../utils/transactionHelpers'
import AppLayout from '../components/AppLayout'

const FILTERS = [
  { label: 'Todos',          value: '' },
  { label: 'Depositos',      value: 'DEPOSIT' },
  { label: 'Transferencias', value: 'TRANSFER' },
  { label: 'Retiros',        value: 'WITHDRAWAL' },
]

const LIST_HEIGHT = 430

function TransactionHistory() {
  const [type, setType] = useState('')
  const { history, page, setPage, totalPages, historyError, isLoading } = useTransactionHistory({ size: 7, type })

  return (
    <AppLayout>
      <div className="p-6 flex flex-col gap-5">

        {/* Header */}
        <div>
          <h1 className="text-lg font-medium text-gray-900">Movimientos</h1>
          <p className="text-xs text-gray-400 mt-0.5">Historial completo de tu cuenta</p>
        </div>

        {/* Filtros */}
        <div className="flex gap-2">
          {FILTERS.map((f) => (
            <button
              key={f.value}
              onClick={() => setType(f.value)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition cursor-pointer ${
                type === f.value
                  ? 'bg-slate-900 text-white'
                  : 'bg-white border border-gray-200 text-gray-500 hover:border-gray-300'
              }`}
            >
              {f.label}
            </button>
          ))}
        </div>

        {/* Error */}
        {historyError && (
          <div className="px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
            {historyError}
          </div>
        )}

        {/* Lista con altura fija equivalente a 7 rows */}
        <div
          className="bg-white rounded-xl border border-gray-100 overflow-hidden"
          style={{ minHeight: `${LIST_HEIGHT}px` }}
        >
          {isLoading ? (
            <div className="flex flex-col">
              {[...Array(7)].map((_, i) => (
                <div key={i} className="flex justify-between items-center px-4 py-3 border-b border-gray-50 last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-gray-100 rounded-lg animate-pulse" />
                    <div className="flex flex-col gap-1.5">
                      <div className="h-3 w-28 bg-gray-100 rounded animate-pulse" />
                      <div className="h-2.5 w-20 bg-gray-50 rounded animate-pulse" />
                    </div>
                  </div>
                  <div className="h-3 w-16 bg-gray-100 rounded animate-pulse" />
                </div>
              ))}
            </div>
          ) : history.length === 0 ? (
            <p className="text-sm text-gray-400 text-center py-12">No hay movimientos para mostrar.</p>
          ) : (
            history.map((t) => {
              const { bg, color, icon } = getIconConfig(t)
              return (
                <div key={t.id} className="flex justify-between items-center px-4 py-3 border-b border-gray-50 last:border-0">
                  <div className="flex items-center gap-3">
                    <div className={`w-8 h-8 ${bg} rounded-lg flex items-center justify-center shrink-0`}>
                      <svg xmlns="http://www.w3.org/2000/svg" className={`w-4 h-4 ${color}`} fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d={icon} />
                      </svg>
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-800">{getLabel(t)}</p>
                      <p className="text-xs text-gray-400">{formatDate(t.createdAt)}</p>
                    </div>
                  </div>
                  <span className={`text-sm font-medium ${getAmountColor(t)}`}>
                    {getAmountPrefix(t)}${Number(t.amount).toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                  </span>
                </div>
              )
            })
          )}
        </div>

        {/* Paginacion: siempre en el mismo lugar */}
        <div className="flex items-center justify-center gap-4">
          <button
            disabled={page === 0}
            onClick={() => setPage(page - 1)}
            className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
          >
            Anterior
          </button>

          <div className="px-4 py-2 bg-slate-900 rounded-lg">
            <span className="text-sm font-medium text-white">{page + 1}</span>
            <span className="text-sm text-slate-400 mx-1">/</span>
            <span className="text-sm text-slate-300">{totalPages}</span>
          </div>

          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage(page + 1)}
            className="px-4 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg text-sm font-medium transition disabled:bg-blue-300 disabled:cursor-not-allowed cursor-pointer"
          >
            Siguiente
          </button>
        </div>

      </div>
    </AppLayout>
  )
}

export default TransactionHistory