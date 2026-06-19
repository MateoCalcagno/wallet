import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import api from '../api/axios'
import FormPage from '../components/FormPage'
import IconInput from '../components/IconInput'

function Profile() {
  const [alias, setAlias] = useState('')
  const [currentAlias, setCurrentAlias] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api.get('/users/me').then(res => {
      setCurrentAlias(res.data.alias || '')
      setAlias(res.data.alias || '')
    }).catch(() => navigate('/login'))
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSuccess(false)

    try {
      await api.patch('/wallet/alias', { alias })
      setCurrentAlias(alias)
      setSuccess(true)
      setTimeout(() => navigate('/dashboard'), 1500)
    } catch (err) {
      const msg = err.response?.data?.message || ''
      if (msg.toLowerCase().includes('already in use')) {
        setError('Ese alias ya está en uso, elegí otro')
      } else {
        setError(msg || 'Error al actualizar el alias')
      }
    }
  }

  return (
    <FormPage panelTitle={"Tu alias,\ntu identidad."} panelSubtitle="Personalizá tu alias para recibir transferencias más fácil.">
          <div className="mb-8">
            <h1 className="text-xl font-medium text-gray-900 mb-1">Editar alias</h1>
            <p className="text-sm text-gray-500">
              Alias actual: <span className="font-mono text-gray-700">{currentAlias || '...'}</span>
            </p>
          </div>

          {error && (
            <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-600">
              {error}
            </div>
          )}

          {success && (
            <div className="mb-4 px-4 py-3 bg-green-50 border border-green-200 rounded-lg text-sm text-green-600">
              ¡Alias actualizado correctamente!
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-gray-500 mb-1.5">Nuevo alias</label>
              <IconInput
                type="text"
                value={alias}
                onChange={(e) => setAlias(e.target.value.toLowerCase())}
                placeholder="palabra.palabra.palabra"
                className="font-mono"
                iconPath="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
              />
              <p className="text-xs text-gray-400 mt-1.5">
                Podés usar letras, números, puntos y guiones.
              </p>
            </div>

            <button
              type="submit"
              className="w-full bg-blue-600 hover:bg-blue-700 text-white py-2.5 rounded-lg text-sm font-medium transition mt-2 cursor-pointer"
            >
              Guardar alias
            </button>

            <button
              type="button"
              onClick={() => navigate('/dashboard')}
              className="w-full text-sm text-gray-500 hover:text-gray-700 transition py-1 cursor-pointer"
            >
              ← Volver al inicio
            </button>
          </form>
    </FormPage>
  )
}

export default Profile